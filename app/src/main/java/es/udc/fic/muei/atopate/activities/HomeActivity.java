package es.udc.fic.muei.atopate.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.fragments.HistorialFragment;
import es.udc.fic.muei.atopate.fragments.HomeFragment;
import es.udc.fic.muei.atopate.fragments.TrayectoFragment;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private BottomNavigationView bottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            boolean isAlreadyChecked = checkIfItemIsAlreadyChecked(item, bottomNavigationView);

            if (isAlreadyChecked) {
                return true;
            }

            boolean validNavigationItemSelected = false;
            Fragment fragmentToSubstitute = HomeFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:

                    fragmentToSubstitute = HomeFragment.newInstance();

                    validNavigationItemSelected = true;
                    break;

                case R.id.navigation_atopate:

                    fragmentToSubstitute = TrayectoFragment.newInstance();

                    validNavigationItemSelected = true;
                    break;

                case R.id.navigation_resumen:

                    fragmentToSubstitute = HomeFragment.newInstance();

                    validNavigationItemSelected = true;
                    break;

                case R.id.navigation_historico:

                    fragmentToSubstitute = HistorialFragment.getInstance();

                    validNavigationItemSelected = true;
                    break;

                case R.id.navigation_ajustes:

                    fragmentToSubstitute = HomeFragment.newInstance();

                    validNavigationItemSelected = true;
                    break;
            }

            transaction.replace(R.id.general_fragment_container, fragmentToSubstitute);
            transaction.commit();

            return validNavigationItemSelected;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeFirstFragment();

        configureBottomNavigation();
    }

    private boolean checkIfItemIsAlreadyChecked(MenuItem checkedItem, BottomNavigationView navigationView) {

        Menu menu = navigationView.getMenu();

        // recorremos los items del menu en busca del que ha sido pulsado para ver si ya estaba pulsado
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);

            boolean sameId = checkedItem.getItemId() == menuItem.getItemId();

            if (sameId && menuItem.isChecked()) {
                return true;
            }
        }

        return false;

    }


    private void initializeFirstFragment() {
        Fragment fragmentToSubstitute = HomeFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.general_fragment_container, fragmentToSubstitute);
        transaction.commit();
    }


    private void configureBottomNavigation() {
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

}
