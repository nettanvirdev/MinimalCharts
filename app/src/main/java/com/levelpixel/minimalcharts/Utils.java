
/*
 * Copyright © 2025 Tanvir Ahamed
 *  All rights reserved.
 *
 *  This software is developed and maintained by Tanvir Ahamed, CEO and Founder of LevelPixela.
 *  Unauthorized copying, modification, distribution, or use of this software in any medium is strictly prohibited.
 *
 *  For inquiries, permissions, or contributions, contact LevelPixela at [https://www.levelpixel.net].
 *
 */

package com.levelpixel.minimalcharts;

import android.content.Context;

public class Utils {

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
