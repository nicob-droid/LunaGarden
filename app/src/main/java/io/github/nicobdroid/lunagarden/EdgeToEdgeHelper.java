package io.github.nicobdroid.lunagarden;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Centralise la configuration Edge-to-Edge et l'application des insets systeme.
 */
public final class EdgeToEdgeHelper {

    private EdgeToEdgeHelper() {
        // Utility class.
    }

    public static void apply(@NonNull AppCompatActivity activity, @IdRes int rootViewId) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        View root = activity.findViewById(rootViewId);
        if (root != null) {
            applySystemBarInsets(root);
        }
    }

    public static void applyToContentRoot(@NonNull AppCompatActivity activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        View content = activity.findViewById(android.R.id.content);
        if (content != null) {
            applySystemBarInsets(content);
        }
    }

    private static void applySystemBarInsets(@NonNull View view) {
        final int initialLeft = view.getPaddingLeft();
        final int initialTop = view.getPaddingTop();
        final int initialRight = view.getPaddingRight();
        final int initialBottom = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    initialLeft + systemBars.left,
                    initialTop + systemBars.top,
                    initialRight + systemBars.right,
                    initialBottom + systemBars.bottom
            );
            return insets;
        });
        ViewCompat.requestApplyInsets(view);
    }
}

