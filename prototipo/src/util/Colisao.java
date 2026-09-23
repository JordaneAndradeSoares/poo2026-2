package util;

import java.awt.Rectangle;

public final class Colisao {
    private Colisao() {}

    public static boolean sobrepoe(Rectangle a, Rectangle b) {
        return a.intersects(b);
    }
}
