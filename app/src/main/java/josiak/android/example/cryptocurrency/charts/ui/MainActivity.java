package josiak.android.example.cryptocurrency.charts.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import josiak.android.example.cryptocurrency.charts.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavHostFragment hostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);

        NavController navController = hostFragment.getNavController();
        //setupActionBar(navController);
        setupBottomNavigation(navController);
    }

    private void setupBottomNavigation(NavController navController){
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_nav_view);
        NavigationUI.setupWithNavController(bottomNavigation, navController);
    }

    private void setupActionBar(NavController navController){
        NavigationUI.setupActionBarWithNavController(this, navController, null);
    }

}
