package layout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.login.widget.LoginButton;

import in.emoji.emosyn.R;

public class LoginScreen extends AppCompatActivity {

    LoginButton Fbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Fbutton=(LoginButton)findViewById(R.id.Fbutton);
        Fbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
