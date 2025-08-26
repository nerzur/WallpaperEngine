package com.nerzur.wallpaperengine.util.windowsNotifier;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class WindowsNotifier {

    public static TrayIcon trayIcon;

    public static void showNotification(String title, String message) {
        try {

            showModernToast(title, message);

        } catch (Exception e) {
            e.printStackTrace();
            showPowerShellNotification(title, message);
        }
    }

    private static void showModernToast(String title, String message) throws IOException, InterruptedException {
        String xml = String.format(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<toast>" +
                        "<visual>" +
                        "<binding template=\"ToastGeneric\">" +
                        "<text>%s</text>" +
                        "<text>%s</text>" +
                        "</binding>" +
                        "</visual>" +
                        "</toast>",
                title, message
        );

        String script = String.format(
                "Add-Type -AssemblyName System.Runtime.WindowsRuntime\n" +
                        "$asTask = ([System.WindowsRuntimeSystemExtensions].GetMethods() | " +
                        "Where-Object { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and " +
                        "$_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]\n" +
                        "function Await($WinRtTask, $ResultType) {\n" +
                        "    $asTaskGeneric = $asTask.MakeGenericMethod($ResultType)\n" +
                        "    $netTask = $asTaskGeneric.Invoke($null, @($WinRtTask))\n" +
                        "    $netTask.Wait(-1) | Out-Null\n" +
                        "    $netTask.Result\n" +
                        "}\n" +
                        "[Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType = WindowsRuntime] | Out-Null\n" +
                        "[Windows.Data.Xml.Dom.XmlDocument, Windows.Data.Xml.Dom.XmlDocument, ContentType = WindowsRuntime] | Out-Null\n" +
                        "$xml = New-Object Windows.Data.Xml.Dom.XmlDocument\n" +
                        "$xml.LoadXml(@'\n%s\n'@)\n" +
                        "$toast = New-Object Windows.UI.Notifications.ToastNotification $xml\n" +
                        "$notifier = [Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('Windows.ImmersiveControlPanel_cw5n1h2txyewy!microsoft.windows.immersivecontrolpanel')\n" +
                        "Await ($notifier.Show($toast)) ([Windows.UI.Notifications.ToastNotification])",
                xml
        );

        executePowerShell(script);
    }

    private static void showPowerShellNotification(String title, String message) {
        String script = String.format(
                "Add-Type -AssemblyName System.Windows.Forms\n" +
                        "$notify = New-Object System.Windows.Forms.NotifyIcon\n" +
                        "$notify.Icon = [System.Drawing.SystemIcons]::Information\n" +
                        "$notify.BalloonTipIcon = 'Info'\n" +
                        "$notify.BalloonTipText = '%s'\n" +
                        "$notify.BalloonTipTitle = '%s'\n" +
                        "$notify.Visible = $true\n" +
                        "$notify.ShowBalloonTip(10000)\n" +
                        "Start-Sleep -Seconds 10\n" +
                        "$notify.Dispose()",
                message.replace("'", "''"),
                title.replace("'", "''")
        );

        executePowerShell(script);
    }

    private static void executePowerShell(String script) {
        try {
            Path scriptFile = Files.createTempFile("notify", ".ps1");
            Files.write(scriptFile, script.getBytes());

            Process process = new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-ExecutionPolicy", "Bypass",
                    "-File",
                    scriptFile.toString()
            ).start();

            process.waitFor();
            Files.deleteIfExists(scriptFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void showTrayNotification(String title, String message) {
//        if (trayIcon != null) {
//            try {
//                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
//            } catch (Exception e) {
//                System.err.println("Error mostrando notificación: " + e.getMessage());
//            }
//        }
//    }
}