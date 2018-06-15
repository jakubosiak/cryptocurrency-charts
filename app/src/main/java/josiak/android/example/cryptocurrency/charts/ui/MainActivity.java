package josiak.android.example.cryptocurrency.charts.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.databinding.NavigationActivityBinding;

public class MainActivity extends AppCompatActivity {
    private NavigationActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        binding = DataBindingUtil.setContentView(this, R.layout.navigation_activity);
        NavHostFragment hostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        setSupportActionBar(binding.toolbar);

        NavController navController = hostFragment.getNavController();
        setupActionBar(navController);
        setupBottomNavigation(navController);
    }

    private void setupBottomNavigation(NavController navController) {
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);
    }

    private void setupActionBar(NavController navController) {
        NavigationUI.setupActionBarWithNavController(this, navController, null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(null,
                Navigation.findNavController(this, R.id.my_nav_host_fragment));
    }
}
