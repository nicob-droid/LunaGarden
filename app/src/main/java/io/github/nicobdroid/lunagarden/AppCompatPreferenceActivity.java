package io.github.nicobdroid.lunagarden;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Legacy base class kept for source compatibility.
 *
 * New preference screens should rely on {@link androidx.preference.PreferenceFragmentCompat}
 * hosted in a regular {@link AppCompatActivity}.
 */
@Deprecated
public abstract class AppCompatPreferenceActivity extends AppCompatActivity {
}
