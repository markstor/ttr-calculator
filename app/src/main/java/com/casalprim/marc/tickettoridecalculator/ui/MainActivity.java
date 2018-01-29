package com.casalprim.marc.tickettoridecalculator.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.casalprim.marc.tickettoridecalculator.R;
import com.casalprim.marc.tickettoridecalculator.game.Edge;
import com.casalprim.marc.tickettoridecalculator.game.Game;
import com.casalprim.marc.tickettoridecalculator.game.Player;
import com.casalprim.marc.tickettoridecalculator.game.RouteCard;
import com.casalprim.marc.tickettoridecalculator.ui.fragments.CardsFragment;
import com.casalprim.marc.tickettoridecalculator.ui.fragments.MapFragment;
import com.casalprim.marc.tickettoridecalculator.ui.fragments.NoPlayersFragment;
import com.casalprim.marc.tickettoridecalculator.ui.fragments.PlayersFragment;
import com.casalprim.marc.tickettoridecalculator.ui.fragments.ScoreBoardFragment;

import java.io.InputStream;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GameEditionByFragmentListener {
    private static String ARG_GAME = "game";
    private static String ARG_MENU_ITEM_POSITION = "menuItemPosition";
    private Game game;
    private int menuItemSelectedPosition;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            game = (Game) savedInstanceState.getSerializable(ARG_GAME);
            menuItemSelectedPosition = savedInstanceState.getInt(ARG_MENU_ITEM_POSITION);
        } else {
            // Initialize members with default values for a new instance
            initGame();
            menuItemSelectedPosition = 0;
        }

        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDrawerLayout = findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                 /* host Activity */
                mDrawerLayout,     /* DrawerLayout object */
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //select option in navigation drawer
        onNavigationItemSelected(navigationView.getMenu().getItem(menuItemSelectedPosition));


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapFragmentClick() {
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.map));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            if (navigationView.getMenu().getItem(i).equals(menuItem)) {
                menuItemSelectedPosition = i;
                break;
            }
        }

        switch (menuItem.getItemId()) {
            case R.id.players:
                fragment = PlayersFragment.newInstance(this.game.getPlayers());
                break;
            case R.id.map:
                fragment = MapFragment.newInstance(this.game.getPlayers(), this.game.getGameMap());
                if (this.game.getPlayers().isEmpty())
                    fragment = new NoPlayersFragment();
                break;
            case R.id.cards:
                fragment = CardsFragment.newInstance(this.game.getPlayers(), this.game.getRouteCards());
                break;
            case R.id.scoreboard:
//                for(Player player : this.game.getPlayers().values()){
//                    player.computeLongestPath();
//                }
                fragment = ScoreBoardFragment.newInstance(this.game.getPlayers());
                break;
            default:
                fragment = null;//PlayersFragment.class;
        }
        if (fragment != null) {

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
        }
        // Close the navigation drawer
        mDrawerLayout.closeDrawers();

        return true;
    }

    @Override
    public void onPlayerAdded(Player.PlayerColor color) {
        if (!game.getPlayers().containsKey(color)) { //if player not in the game
            game.addNewPlayer(color);
            Player player = game.getPlayers().get(color);
            String name = "";
            switch (player.getColor()) {
                case BLUE:
                    name = getString(R.string.blue_player_name);
                    break;
                case RED:
                    name = getString(R.string.red_player_name);
                    break;
                case GREEN:
                    name = getString(R.string.green_player_name);
                    break;
                case YELLOW:
                    name = getString(R.string.yellow_player_name);
                    break;
                case BLACK:
                    name = getString(R.string.black_player_name);
                    break;
            }
            player.setName(name);
        }
    }

    @Override
    public void onPlayerRemoved(Player.PlayerColor color) {
        if (game.getPlayers().containsKey(color)) { //if player in the game
            game.removePlayer(color);
        }
        if (game.getPlayers().isEmpty()) {
            initGame();
            Toast.makeText(this, "Game reset", Toast.LENGTH_SHORT).show();
        }
    }

    private void initGame() {
        InputStream mapInputStream = getResources().openRawResource(R.raw.map_eur);
        InputStream cardsInputStream = getResources().openRawResource(R.raw.routecards_eur);
        game = new Game(mapInputStream, cardsInputStream);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARG_GAME, this.game);
        outState.putInt(ARG_MENU_ITEM_POSITION, this.menuItemSelectedPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRouteCardAssigned(Player.PlayerColor color, RouteCard card) {
        game.assignRouteCard(color, card);
        notifyCardFragments();
    }

    @Override
    public void onRouteCardUnassigned(Player.PlayerColor color, RouteCard card) {
        game.unassignRouteCard(color, card);
        notifyCardFragments();
    }

    @Override
    public void onTrainAdded(final Player.PlayerColor color, Edge edge) {
        try {
            this.game.addTrain(color, edge);
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTrainRemoved(Player.PlayerColor color, Edge edge) {
        this.game.removeTrain(color, edge);
    }

    public void notifyCardFragments() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.flContent);
        if (fragment instanceof CardsFragment) {
            ((CardsFragment) fragment).notifyAllFragments();
        }
    }

}
