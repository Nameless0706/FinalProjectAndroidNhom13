package com.nhom13.phonemart;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.service.UserService;
import com.nhom13.phonemart.ui.auth.LoginFragment;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.TokenUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // khi logout là xóa hết dữ liệu trong file auth nên chỉ cần check accessToken là đủ
        String accessToken = TokenUtils.getAccessToken(MainActivity.this);
        Long userId = TokenUtils.getUserId(MainActivity.this);

        if (TextUtils.isEmpty(accessToken)) {
            FragmentUtils.loadFragment(getSupportFragmentManager(), R.id.main_frag_container, new LoginFragment());
        } else {
            getUserById(userId);
        }
    }

    private void getUserById(Long userId) {
        UserService userService = new UserService(MainActivity.this);
        userService.getUserDto(userId, new GeneralCallBack<UserDto>() {
            // refreshToken còn hạn nên chỉ việc call API lại để lấy UserDto
            @Override
            public void onSuccess(UserDto result) {
                FragmentUtils.loadFragment(MainActivity.this.getSupportFragmentManager(), R.id.main_frag_container, BaseFragment.newInstance(result));
            }

            // refreshToken hết hạn nên chuyển về LoginFragment và xuất Dialog
            @Override
            public void onError(Throwable t) {
                FragmentUtils.loadFragment(getSupportFragmentManager(), R.id.main_frag_container, new LoginFragment());
            }
        });
    }
}