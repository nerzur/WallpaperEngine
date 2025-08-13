package cu.havanaclub.wallpaperengine.util.WallpaperChanger;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.win32.StdCallLibrary;

import static com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER;

public class WallpaperChanger {

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);

        // Cambia esto:
        boolean SystemParametersInfoA(
                int uiAction,
                int uiParam,
                String pvParam,
                int fWinIni
        );

        // O alternativamente para Unicode:
        boolean SystemParametersInfoW(
                int uiAction,
                int uiParam,
                String pvParam,
                int fWinIni
        );
    }

    public static void setWallpaper(String imagePath) {
        try {
            // Configurar en el registro de Windows
            Advapi32Util.registrySetStringValue(
                    HKEY_CURRENT_USER,
                    "Control Panel\\Desktop",
                    "Wallpaper",
                    imagePath
            );

            // Configurar estilo (10 = Rellenado)
            Advapi32Util.registrySetIntValue(
                    HKEY_CURRENT_USER,
                    "Control Panel\\Desktop",
                    "WallpaperStyle",
                    10
            );

            Advapi32Util.registrySetStringValue(
                    HKEY_CURRENT_USER,
                    "Control Panel\\Desktop",
                    "TileWallpaper",
                    "0"
            );

            // Notificar al sistema del cambio
            User32.INSTANCE.SystemParametersInfoA( // o SystemParametersInfoW
                    0x0014, // SPI_SETDESKWALLPAPER
                    0,
                    imagePath,
                    0x01 | 0x02
            );

            System.out.println("Fondo de pantalla cambiado exitosamente");
        } catch (Exception e) {
            System.err.println("Error al cambiar el fondo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Cambia esta ruta por la ubicación de tu imagen
        String imagePath = "C:\\Users\\TuUsuario\\Pictures\\fondo.jpg";

        // Verificar si el archivo existe antes de intentar cambiarlo
        if (new java.io.File(imagePath).exists()) {
            setWallpaper(imagePath);
        } else {
            System.err.println("El archivo no existe: " + imagePath);
        }
    }
}
