package bo.gob.ine.naci.epc.herramientas;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import bo.gob.ine.naci.epc.entidades.DataBase;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.Upm;
import bo.gob.ine.naci.epc.entidades.Usuario;

public class ActionBarActivityBluetooth extends ActionBarActivityNavigator {
    protected BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    protected UUID myUUID = UUID.fromString("07d1c62f-da85-41ac-8085-b580bcaf8d84");
    protected BluetoothServerSocket bluetoothServerSocket;
    protected int idUpm;

    public String[] unZip(Uri file) {
        int cont = 0;
        ZipInputStream zis;
        try {
            InputStream is = getContentResolver().openInputStream(file);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                    File fmd = new File(Parametros.DIR_TEMP + ze.getName());
                    if (fmd.mkdirs()) {
                        continue;
                    }
                }
                FileOutputStream fout = new FileOutputStream(Parametros.DIR_TEMP + ze.getName());
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }
                fout.close();
                zis.closeEntry();
                if (ze.getName().equals(Parametros.SIGLA_PROYECTO)) {
                    cont++;
                } else {
                    if (ze.getName().equals("datos.txt")) {
                        cont++;
                    }
                }
            }

            zis.close();
            if (cont == 2) {
                return new String[]{Parametros.DIR_TEMP + Parametros.SIGLA_PROYECTO, Parametros.DIR_TEMP + "datos.txt"};
            } else {
                return new String[]{"Archivo inválido."};
            }
        } catch (IOException ex) {
            return new String[]{ex.getMessage()};
        }
    }

    public String load(String[] args) {
        String mensaje = "";
        try {
            File file = new File(args[1]);
            byte[] bytes = new byte[(int) file.length()];
            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }
            String[] datos = new String(bytes).split("\n");
            Upm upm = new Upm();
            if (upm.abrir(idUpm)) {
                if (upm.get_codigo().equals(datos[0])) {
                    int id = Integer.parseInt(datos[1]);
                    Informante informante = new Informante();
                    try {
//                        informante.insertData(args[0], upm.get_id_upm(), id, new IdInformante(datos[2] + "," + datos[3]), Integer.parseInt(datos[4]));
                    } catch (Exception ex) {
                        mensaje += ex.getMessage();
                    }
                    informante.free();
                    mensaje += "Ok";
                } else {
                    mensaje = "La UPM del archivo no corresponde con la seleccionada.";
                }
            } else {
                mensaje = "No se encontró la UPM.";
            }
            upm.free();
        } catch (IOException ex) {
            mensaje = ex.getMessage();
        }
        return mensaje;
    }

    public String backup(IdInformante idInformante, int idNivel) {
        Upm upm = new Upm();
        if (upm.abrir(idUpm)) {
            Format formatter = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH);
            String res = formatter.format(new Date()) + ".zip";
            String file = DataBase.getFilePathDB();
            String zip = Parametros.DIR_BAK + res;
            try {
                File directory = new File(Parametros.DIR_BAK);
                if (!directory.exists()) {
                    if (!directory.mkdirs()) {
                        errorMessage(ActionBarActivityBluetooth.this,null, "Error!", Html.fromHtml("No se pudo crear el directorio."), Parametros.FONT_OBS);
                    }
                }

                FileOutputStream fos = new FileOutputStream(zip);
                ZipOutputStream zos = new ZipOutputStream(fos);
                ZipEntry ze = new ZipEntry(new File(file).getName());
                zos.putNextEntry(ze);

                FileInputStream fis = new FileInputStream(file);
                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }
                fis.close();
                zos.closeEntry();

                ze = new ZipEntry("datos.txt");
                zos.putNextEntry(ze);
                zos.write((upm.get_codigo() + "\n" + Usuario.getUsuario() + "\n" + idInformante.id_asignacion + "\n" + idInformante.correlativo + "\n" + idNivel).getBytes());
                zos.closeEntry();
                zos.close();
                return res;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            errorMessage(ActionBarActivityBluetooth.this,null, "Error!", Html.fromHtml("No se encontró la UPM."), Parametros.FONT_OBS);
        }
        upm.free();
        return null;
    }

    public class BluetoothServer extends AsyncTask<Void, String, String> {
        protected ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ActionBarActivityBluetooth.this);
            dialog.setMessage("Esperando conexión...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    try {
                        bluetoothServerSocket.close();
                    } catch (IOException ex) {
                        errorMessage(ActionBarActivityBluetooth.this,"cargarListado", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
                    }
                }
            });
            dialog.setTitle("SICE");
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(String... args) {
            dialog.setMessage(args[0]);
            if (args[0].equals("Cambiando editor...")) {
                dialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            if (bluetoothAdapter == null) {
                return "Bluetooth no soportado.";
            }
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            try {
                bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(myUUID.toString(), myUUID);
                if (bluetoothServerSocket == null) {
                    return "No se pudo abrir el socket.";
                } else {
                    String mensaje;
                    BluetoothSocket bluetoothSocket = bluetoothServerSocket.accept();

                    OutputStream outStream = bluetoothSocket.getOutputStream();
                    InputStream inStream = bluetoothSocket.getInputStream();

                    File file;
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int l = inStream.read(buffer);
                        String command = "";
                        for (int i = 0; i < l; i++) {
                            command += (char)buffer[i];
                        }
                        if (command.startsWith("file:")) {
                            publishProgress("Recibiendo datos...");
                            file = new File(Parametros.DIR_TEMP + command.split(":")[1]);
                            int fileSize = Integer.parseInt(command.split(":")[2]);
                            outStream.write("send".getBytes());
                            FileOutputStream fout = new FileOutputStream(file);
                            int bytesReceived = 0;
                            while (bytesReceived < fileSize) {
                                l = inStream.read(buffer);
                                if (l > 0) {
                                    bytesReceived += l;
                                    fout.write(buffer, 0, l);
                                } else {
                                    publishProgress("Read received -1, breaking");
                                    break;
                                }
                            }
                            fout.close();
                            String[] args = unZip(Uri.fromFile(file));
                            if (args.length == 2) {
                                mensaje = load(args);
                                if (mensaje.equals("Ok")) {
                                    outStream.write("received".getBytes());
                                } else {
                                    break;
                                }
                            } else {
                                mensaje = args[0];
                                break;
                            }
                        } else {
                            if (command.startsWith("change:")) {
                                publishProgress("Cambiando editor...");
                                String change = null;
                                String[] ids = command.split(":")[1].split(";");
                                Informante informante = new Informante();
                                for (String id : ids) {
                                    if (informante.abrir(new IdInformante(id))) {
                                        informante.editar();
                                        informante.set_id_usuario(Usuario.getUsuario());
                                        informante.guardar();
                                    } else {
                                        if (change == null) {
                                            change = id;
                                        } else {
                                            change += ";" + id;
                                        }
                                        informante.free();
                                    }
                                }
                                if (change == null) {
                                    outStream.write("changed".getBytes());
                                } else {
                                    outStream.write(("change:" + change).getBytes());
                                }
                            } else {
                                if (command.equals("Ok")) {
                                    mensaje = "Ok";
                                    break;
                                } else {
                                    mensaje = "Final inesperado.";
                                    break;
                                }
                            }
                        }
                    }
                    bluetoothSocket.close();
                    bluetoothServerSocket.close();
                    return mensaje;
                }
            } catch (IOException ex) {
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result.equals("Ok")) {
                exitoMessage(ActionBarActivityBluetooth.this,"cargarListado", "Concluido", Html.fromHtml("Transferencia concluida."), Parametros.FONT_OBS);
            } else {
                errorMessage(ActionBarActivityBluetooth.this,"cargarListado", "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
                endThree();
            }
        }
    }

    public class BluetoothClient extends AsyncTask<String, String, String> {
        protected ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ActionBarActivityBluetooth.this);
            dialog.setMessage("Intentando conectar...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setTitle("SICE");
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(String... args) {
            dialog.setMessage(args[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            if (bluetoothAdapter == null) {
                return "Bluetooth no soportado.";
            }
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            String mensaje;
            try {
                Usuario usuario = new Usuario();
                if (usuario.abrir(Integer.parseInt(params[0]))) {
                    File file = new File(params[1]);
                    String serie = usuario.get_serie();
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(serie);
                    BluetoothSocket bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                    bluetoothSocket.connect();
                    OutputStream outStream = bluetoothSocket.getOutputStream();
                    InputStream inStream = bluetoothSocket.getInputStream();
                    outStream.write(("file:" + file.getName() + ":" + file.length()).getBytes());
                    int l;
                    byte[] buffer = new byte[1024];
                    while (true) {
                        l = inStream.read(buffer);
                        String command = "";
                        for (int i = 0; i < l; i++) {
                            command += (char) buffer[i];
                        }
                        if (command.equals("send")) {
                            FileInputStream fis = new FileInputStream(file);
                            publishProgress("Enviando información...");
                            while ((l = fis.read(buffer, 0, 1024)) > 0) {
                                outStream.write(buffer, 0, l);
                            }
                            fis.close();
                        } else {
                            if (command.equals("received")) {
                                if (params.length == 3) {
                                    publishProgress("Cambiando editor...");
                                    String[] ids = params[2].split(";");
                                    Informante informante = new Informante();
                                    for (String id : ids) {
                                        if (informante.abrir(new IdInformante(id))) {
                                            informante.editar();
                                            informante.set_id_usuario(usuario.get_id_usuario());
                                            informante.guardar();
                                        } else {
                                            informante.free();
                                        }
                                    }
                                    outStream.write(("change:" + params[2]).getBytes());
                                } else {
                                    outStream.write("Ok".getBytes());
                                    mensaje = "Ok";
                                    break;
                                }
                            } else {
                                if (command.startsWith("changed")) {
                                    outStream.write("Ok".getBytes());
                                    mensaje = "Ok";
                                    break;
                                } else {
                                    mensaje = "Reenvíe la boleta al usuario.";
                                    break;
                                }
                            }
                        }
                    }
                    bluetoothSocket.close();
                } else {
                    mensaje = "No se encontró el usuario.";
                }
                usuario.free();
            } catch (IOException ex) {
                mensaje = ex.getMessage();
            }
            return mensaje;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result.equals("Ok")) {
                exitoMessage(ActionBarActivityBluetooth.this,"cargarListado", "Concluido", Html.fromHtml("Transferencia concluida."), Parametros.FONT_OBS);
            } else {
                errorMessage(ActionBarActivityBluetooth.this,"cargarListado", "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
                endThree();
            }
        }
    }
}
