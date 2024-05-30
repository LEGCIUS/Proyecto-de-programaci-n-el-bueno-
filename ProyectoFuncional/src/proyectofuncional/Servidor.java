/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package proyectofuncional;



import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Esta es la clase servidor, encargada de la administracion del server
 * @author dng
 */
public class Servidor extends Application {

    public static Vector<Flujo> usuarios = new Vector<>();
    public static final String CARPETA_DESTINO = "C:\\Users\\usuario\\Music\\Documentos\\";
    public static final String musicServer = "C:\\Users\\usuario\\Music\\MusicServer\\";
    public static final String videoServer = "C:\\Users\\usuario\\Music\\VideoServer\\";
    public static final String imageServer = "C:\\Users\\usuario\\Music\\ImagenesServer\\";
    private Socket socket;
    private DataOutputStream SalidaSocket;
/**
 * este es el mentodo main que ejecuta todas las cosas
 * @param args 
 */
    public static void main(String[] args) {
        Servidor.launch(args);
    }

    
   /**
    * Metodo que inicia la ventana de servidor, inicia el servidor y muestra
    * la pantalla admin
    * @param primaryStage 
    */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Servidor de Archivos");
        iniciarServidor(primaryStage);
        mostrarPantallaAdministrador(primaryStage);
    }

    
/**
 * metodo que se encarga de iniciar el servidor y aceptar los usuarios,ademas de
 * empezar el flujo
 * @param primaryStage 
 */
    private void iniciarServidor(Stage primaryStage) {
        Thread serverThread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8000);
                System.out.println("Servidor iniciado. Esperando cliente...");
                while (true) {
                    Socket nsfd = serverSocket.accept();
                    System.out.println("Conexión aceptada de: " + nsfd.getInetAddress());
                    socket = nsfd;
                    Flujo flujo = new Flujo(nsfd);
                    flujo.start();
                }
            } catch (IOException ioe) {
                System.out.println("Error al iniciar el servidor: " + ioe.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    /**
     * metodo que se encarga de la subida al servidor de los archivos
     * @param primaryStage 
     */
    private void cargarArchivo(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo para Cargar");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                File carpetaDestino = new File(CARPETA_DESTINO);
                String extension = getFileExtension(file.getName());
                File carpeta = getCarpetaPorFormato(extension);
                FileInputStream inputStream = new FileInputStream(file);
                FileOutputStream outputStream = new FileOutputStream(new File(carpeta, file.getName()));
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();
                System.out.println("El archivo se ha cargado exitosamente en el servidor.");
            } catch (IOException e) {
                System.out.println("Error al cargar el archivo en el servidor: " + e.getMessage());
            }
        }
    }

    /**
     * este metodod obtiene las extenciones de los archivos 
     * @param fileName
     * @return 
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return "";
    }

    /**
     * este metodo se encarga de guardar los archivos recibidos en las carpetas
     * correspondiente a su extencion 
     * @param extension
     * @return 
     */
    private File getCarpetaPorFormato(String extension) {
        extension = extension.toLowerCase();
        File carpeta;

        if (extension.equals(".mp3")) {
            carpeta = new File(Servidor.musicServer);
        } else if (extension.equals(".jpg") || extension.equals(".png")) {
            carpeta = new File(Servidor.imageServer);
        } else if (extension.equals(".docx") || extension.equals(".txt") || extension.equals(".pdf")) {
            carpeta = new File(Servidor.CARPETA_DESTINO);
        } else {
            carpeta = new File(Servidor.videoServer);
        }

        return carpeta;
    }
   /**
    * Este metodo tiene se encarga de obtener la lista de los archvios de la 
    * carpeta
    * @param carpetaDestino
    * @return 
    */
    private List<String> obtenerListaArchivos(String carpetaDestino) {
        List<String> archivos = new ArrayList<>();
        File carpeta = new File(carpetaDestino);
        File[] listaArchivos = carpeta.listFiles();

        if (listaArchivos != null) {
            for (File archivo : listaArchivos) {
                if (archivo.isFile()) {
                    archivos.add(archivo.getName());
                }
            }
        }
        return archivos;
    }

    /**
     * Este metodo pretendia enviarle las cosas al cliente "no sirvio"
     * @param socket
     * @param carpetaDestino
     * @throws IOException 
     */
    private void enviarListaArchivosAlCliente(Socket socket, String carpetaDestino) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        List<String> listaArchivos = obtenerListaArchivos(carpetaDestino);
        outputStream.writeObject(listaArchivos);
        outputStream.flush();
    }

    /**
     * Este metodo se encarga de la verificacion de usuarios con el txt
     * @param username
     * @param password
     * @param tipoUsuario
     * @return 
     */
    public static boolean verificarUsuario(String username, String password, String tipoUsuario) {
        try ( BufferedReader br = new BufferedReader(new FileReader(CARPETA_DESTINO + "C:\\Users\\usuario\\Music\\Usuarios.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Usuario: " + username)) {
                    String passLine = br.readLine();
                    String tipoLine = br.readLine();
                    if (passLine.equals("Contraseña: " + password) && tipoLine.equals("TipoUsuario: " + tipoUsuario)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de usuarios: " + e.getMessage());
        }
        return false;
    }

    /**
     * Este metodo lista los archivos
     * @param carpetaDestino
     * @return 
     */
    private ObservableList<String> listarArchivos(String carpetaDestino) {
        File carpeta = new File(carpetaDestino);
        File[] listaArchivos = carpeta.listFiles();
        ObservableList<String> items = FXCollections.observableArrayList();
        if (listaArchivos != null) {
            for (File archivo : listaArchivos) {
                items.add(archivo.getName());
            }
        }
        return items;
    }

    /**
     * Este metodo se encarga de mostrar la pantalla del admin
     * @param primaryStage 
     */
    public void mostrarPantallaAdministrador(Stage primaryStage) {
        primaryStage.setTitle("Panel de Administrador");
        Button btnDocumentos = new Button("Documentos");
        Button btnImagenes = new Button("Imágenes");
        Button btnVideos = new Button("Videos");
        Button btnMusica = new Button("Música");

        btnDocumentos.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnImagenes.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnVideos.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnMusica.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");

        btnDocumentos.setOnAction(event -> mostrarListView(Servidor.CARPETA_DESTINO, "Documentos", primaryStage));
        btnImagenes.setOnAction(event -> mostrarListView(Servidor.imageServer, "Imágenes", primaryStage));
        btnVideos.setOnAction(event -> mostrarListView(Servidor.videoServer, "Videos", primaryStage));
        btnMusica.setOnAction(event -> mostrarListView(Servidor.musicServer, "Música", primaryStage));

        VBox buttonBox = new VBox(10, btnDocumentos, btnImagenes, btnVideos, btnMusica);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(buttonBox);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    /**
     * Este metodo es para mostrar el listview al admin y posee los botones
     * @param carpeta
     * @param titulo
     * @param primaryStage 
     */
    private void mostrarListView(String carpeta, String titulo, Stage primaryStage) {

        ListView<String> listView = new ListView<>();
        listView.setItems(listarArchivos(carpeta));

        Button btnEliminar = new Button("Eliminar");
        Button btnModificar = new Button("Modificar");
        Button btnSubirArchivo = new Button("Subir Archivo");
        Button btnActualizar = new Button("Actualizar Lista");
        Button btnVolverAdmin = new Button("Volver a Administrador");

        btnEliminar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnModificar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnSubirArchivo.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnActualizar.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
        btnVolverAdmin.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white;");

        btnEliminar.setOnAction(event -> eliminarArchivo(listView, carpeta));
        btnModificar.setOnAction(event -> modificarArchivo(listView, carpeta));
        btnSubirArchivo.setOnAction(event -> cargarArchivo(primaryStage));
        btnActualizar.setOnAction(event -> listView.setItems(listarArchivos(carpeta)));
        btnVolverAdmin.setOnAction(event -> mostrarPantallaAdministrador(primaryStage));

        HBox buttonBox = new HBox(10, btnEliminar, btnModificar, btnSubirArchivo, btnActualizar, btnVolverAdmin);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(listView);
        root.setBottom(buttonBox);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Archivos - " + titulo);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void eliminarArchivo(ListView<String> listView, String carpeta) {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            File archivoAEliminar = new File(carpeta, selectedItem);
            if (archivoAEliminar.delete()) {
                System.out.println("El archivo se ha eliminado exitosamente.");
                listView.setItems(listarArchivos(carpeta));
            } else {
                System.out.println("No se pudo eliminar el archivo.");
            }
        } else {
            System.out.println("Seleccione un archivo para eliminar.");
        }
    }

    private void modificarArchivo(ListView<String> listView, String carpeta) {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            TextInputDialog nameDialog = new TextInputDialog(selectedItem);
            nameDialog.setTitle("Modificar Nombre de Archivo");
            nameDialog.setHeaderText("Ingrese el nuevo nombre para el archivo:");
            nameDialog.setContentText("Nuevo Nombre:");

            Optional<String> newNameResult = nameDialog.showAndWait();
            newNameResult.ifPresent(newName -> {
                File oldFile = new File(carpeta, selectedItem);
                File newFile = new File(carpeta, newName);
                if (oldFile.renameTo(newFile)) {
                    System.out.println("El nombre del archivo se ha modificado exitosamente.");
                    listView.setItems(listarArchivos(carpeta));
                } else {
                    System.out.println("No se pudo modificar el nombre del archivo.");
                }
            });
        } else {
            System.out.println("Seleccione un archivo para modificar.");
        }
    }
}