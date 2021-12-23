package com.android.Luvio1.activities.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;

import com.android.Luvio1.R;
import com.android.Luvio1.activities.Auth.SignInActivity;
import com.android.Luvio1.activities.Main.MainActivity;
import com.android.Luvio1.databinding.ActivitySettingBinding;
import com.android.Luvio1.firebase.DBUserManager;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import io.getstream.chat.android.client.ChatClient;

public class SettingActivity extends ThemeChangeActivity {
    private ActivitySettingBinding binding;
    PreferenceManager preferenceManager;
    ChatClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        client = ChatClient.instance();
        setContentView(binding.getRoot());
        setListener();
    }

    private void setListener() {

        int theme = android.preference.PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .getInt("Theme", R.style.Theme_Luvio);
        if (theme == R.style.DarkTheme) {
            binding.btnDarkMode.setChecked(true);
        }

        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });
        binding.btnSignOut.setOnClickListener(view -> {
            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, false);
            client.disconnect();
            preferenceManager.clear();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });
        binding.btnDeleteAccount.setOnClickListener(view -> {
            loading(true);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.KEY_COLLECTION_USER)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .update(Constants.KEY_IS_DELETE, true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loading(false);
                                DBUserManager dbUserManager = new DBUserManager();
                                client.disconnect();
                                dbUserManager.remove(preferenceManager.getString(Constants.KEY_USER_ID));
                                showToast("Tài khoản đã xóa thành công");
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                preferenceManager.clear();
                                startActivity(intent);

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loading(false);
                            showToast(e.getMessage());
                        }
                    });

        });

        binding.btnBlockList.setOnClickListener(view -> {
            Intent intent = new Intent(this, BlockListActivity.class);
            startActivity(intent);
        });

        binding.btnDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    android.preference.PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext())
                            .edit().putInt("Theme", R.style.DarkTheme).commit();
                    ThemeChangeApply();
                } else {
                    android.preference.PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext())
                            .edit().putInt("Theme", R.style.Theme_Luvio).commit();
                    ThemeChangeApply();

                }
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.btnDeleteAccount.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.btnDeleteAccount.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    void ThemeChangeApply() {
        TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, MainActivity.class))
                .addNextIntent(this.getIntent())
                .startActivities();
    }

}
