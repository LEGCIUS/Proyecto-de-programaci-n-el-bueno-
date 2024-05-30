/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package proyectofuncional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
/**
 * Esta es la clase cliente
 * @author usuario
 */
public class Cliente extends Application {

    private static Socket sfd = null;
    private static DataOutputStream SalidaSocket;
    private static DataInputStream EntradaSocket;

    private TextField usuarioField;
    private PasswordField contraseñaField;
    private TextField rolField;

    /**
     * metodo que se inicia para mostrar la pantlla de login
     * @param primaryStage 
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cliente");
        primaryStage.setWidth(400);
        primaryStage.setHeight(300);

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 10, 10, 10));

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        Rectangle rectangle = new Rectangle(300, 200);
        rectangle.setArcWidth(30);
        rectangle.setArcHeight(30);
        rectangle.setFill(Color.WHITE);//color del fondo del rectangulo 
        rectangle.setStroke(Color.LIGHTGRAY);//color del borde del rectangulo
        rectangle.setStrokeWidth(2);

        Label usuarioLabel = new Label("Usuario:");
        usuarioField = new TextField();
        Label contraseñaLabel = new Label("Contraseña:");
        contraseñaField = new PasswordField();
        
        Label tipoUsuarioLabel = new Label("Tipo de Usuario:");
        ComboBox<String> tipoUsuarioComboBox = new ComboBox<>();
        tipoUsuarioComboBox.getItems().addAll("Solo Videos", "Solo Imagenes", "Solo Musica", "Solo Documentos", "Ver Todo");

        Button btnLogin = new Button("Iniciar Sesión");
        btnLogin.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnLogin.setOnAction(event -> {
            try {
                login(usuarioField.getText(), contraseñaField.getText(), primaryStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button btnRegister = new Button("Registrarse");
        btnRegister.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white;");
        btnRegister.setOnAction(event -> {
            try {
                CodificacionRegistro(usuarioField.getText(), contraseñaField.getText(), tipoUsuarioComboBox.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        HBox buttonBox = new HBox(10, btnLogin, btnRegister);
        buttonBox.setAlignment(Pos.CENTER);

        gridPane.add(usuarioLabel, 0, 0);
        gridPane.add(usuarioField, 1, 0);
        gridPane.add(contraseñaLabel, 0, 1);
        gridPane.add(contraseñaField, 1, 1);
        gridPane.add(tipoUsuarioLabel, 0, 2);
        gridPane.add(tipoUsuarioComboBox, 1, 2);

        StackPane stackPane = new StackPane(rectangle, gridPane);
        stackPane.setAlignment(Pos.CENTER);

        borderPane.setCenter(stackPane);
        borderPane.setBottom(buttonBox);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            sfd = new Socket("localhost", 8000);// qui se pone l IP
            SalidaSocket = new DataOutputStream(sfd.getOutputStream());
            EntradaSocket = new DataInputStream(sfd.getInputStream());
        } catch (IOException e) {
            System.out.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * este es el metodo que se encarga de logearse
     * @param username
     * @param contraseña
     * @param primaryStage
     * @throws IOException 
     */
    private void login(String username, String contraseña, Stage primaryStage) throws IOException {
        SalidaSocket.writeUTF("login");
        SalidaSocket.writeUTF(username);
        SalidaSocket.writeUTF(contraseña);
        SalidaSocket.flush();

        String respuesta = EntradaSocket.readUTF();
        /*
        aqui comentamos todo lo que no iba a servir si tenia solo la clase 
        cliente para que pueda inicirlo sin problemas
        */
        switch (respuesta) {
            case "VisualizarTodo":
                mostrarPantallaCliente(primaryStage);
                break;
            case "VisualizarVideos":
                // mostrarArchivos("Videos", listarArchivos(Servidor.videoServer));
                mostrarPantallaCliente(primaryStage);
                break;
            case "VisualizarImagenes":
                // mostrarArchivos("Imágenes", listarArchivos(Servidor.imageServer));
                mostrarPantallaCliente(primaryStage);
                break;
            case "VisualizarMusica":
                //mostrarArchivos("Música", listarArchivos(Servidor.musicServer));
                mostrarPantallaCliente(primaryStage);
                break;
            case "VisualizarDocumentos":
                //mostrarArchivos("Documentos", listarArchivos(Servidor.CARPETA_DESTINO));
                mostrarPantallaCliente(primaryStage);
                break;
            case "loginFallido":
                System.out.println("Usuario o contraseña incorrectos.");
                break;
            default:
                break;
        }
    }

    private void CodificacionRegistro(String username, String contraseña, String tipoUsuario) throws IOException {
        SalidaSocket.writeUTF("registro");
        SalidaSocket.writeUTF(username);
        SalidaSocket.writeUTF(contraseña);
        SalidaSocket.writeUTF(tipoUsuario);
        SalidaSocket.flush();

        String respuesta = EntradaSocket.readUTF();
        if (respuesta.equals("registroExitoso")) {
            System.out.println("Registro exitoso.");
        } else {
            System.out.println("El nombre de usuario ya está en uso.");
        }
    }

    private ObservableList<String> listarArchivos(String carpetaDestino) {
        ObservableList<String> archivos = FXCollections.observableArrayList();
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
     * Este es el metodo para mostrar la pantalla cliente
     * @param primaryStage 
     */
    public void mostrarPantallaCliente(Stage primaryStage) {
        primaryStage.setTitle("Categorías de Archivos");

        // Crea los botones
        Button btnVideos = new Button("Videos");
        Button btnImagenes = new Button("Imágenes");
        Button btnDocumentos = new Button("Documentos");
        Button btnMusica = new Button("Música");


        btnVideos.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnImagenes.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnDocumentos.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnMusica.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");

        
        HBox buttonBox = new HBox(10, btnVideos, btnImagenes, btnDocumentos, btnMusica);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        
        VBox root = new VBox();
        root.getChildren().addAll(new javafx.scene.control.Label("Contenido de la pantalla de cliente"), buttonBox);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        // aqui estan las cciones del boton, pero no funciona
//        btnVideos.setOnAction(event -> mostrarArchivos("Videos", listarArchivos(Servidor.videoServer)));
//        btnImagenes.setOnAction(event -> mostrarArchivos("Imágenes", listarArchivos(Servidor.imageServer)));
//        btnDocumentos.setOnAction(event -> mostrarArchivos("Documentos", listarArchivos(Servidor.CARPETA_DESTINO)));
//        btnMusica.setOnAction(event -> mostrarArchivos("Música", listarArchivos(Servidor.musicServer)));
    }
    /**
     * Este metodos se encarga de mostrar los archivos
     * @param titulo
     * @param archivos 
     */
    private void mostrarArchivos(String titulo, javafx.collections.ObservableList<String> archivos) {
        Stage ventanaArchivos = new Stage();
        ventanaArchivos.initModality(Modality.APPLICATION_MODAL);
        ventanaArchivos.setTitle(titulo);

        ListView<String> listView = new ListView<>();
        listView.setItems(archivos);

        // Boton de descarga
        Button btnDescargar = new Button("Descargar");
        btnDescargar.setOnAction(event -> descargarArchivo(listView.getSelectionModel().getSelectedItem()));

        // Maneja la accion al darle doble clic en cualquier archivo de la carpeta
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    switch (titulo) {
                        case "Videos":
//                            reproducirVideo(selectedItem);
                            break;
                        case "Imágenes":
//                            mostrarImagen(selectedItem);
                            break;
                        case "Música":
//                            reproducirMusica(selectedItem);
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        VBox vBox = new VBox(10, listView, btnDescargar);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));

        Scene scene = new Scene(vBox, 300, 200);
        ventanaArchivos.setScene(scene);
        ventanaArchivos.show();
    }
     /**
      * este es el metodo descargr (no funciono)
      * @param archivo 
      */
    private void descargarArchivo(String archivo) { // falta por arreglar este metodo 
        if (archivo != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar archivo");
            fileChooser.setInitialFileName(archivo);

          
            File selectedFile = fileChooser.showSaveDialog(new Stage());
            if (selectedFile != null) {

                System.out.println("Archivo guardado en: " + selectedFile.getAbsolutePath());
            }
        }
    }
/*
    public void reproducirVideo(String videoName) {
        String videoFilePath = Servidor.videoServer + File.separator + videoName;
        Media media = new Media(new File(videoFilePath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        //medidas de la pantalla
        mediaView.setFitWidth(800);
        mediaView.setFitHeight(600);
        mediaView.setPreserveRatio(true);

        StackPane root = new StackPane();
        root.getChildren().add(mediaView);

        
        Scene scene = new Scene(root, 800, 600);
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Video Player");
        primaryStage.setScene(scene);

        Platform.runLater(() -> {
            primaryStage.show();
            // reproduce el video en la pantalla de video
            mediaPlayer.play();
        });
    }

      
    //muestra la imgenes al darle doble click 
    private void mostrarImagen(String imageName) {
        String imageFilePath = Servidor.imageServer + File.separator + imageName;
        Image image = new Image(new File(imageFilePath).toURI().toString());
        ImageView imageView = new ImageView(image);

        StackPane root = new StackPane();
        root.getChildren().add(imageView);

        Scene scene = new Scene(root, 800, 600);
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Image Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void reproducirMusica(String musicName) {
        String musicFilePath = Servidor.musicServer + File.separator + musicName;
        Media media = new Media(new File(musicFilePath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        
        MediaView mediaView = new MediaView(mediaPlayer);

        // aqui creamos los botones de para el reproductor
        Button playButton = new Button("Play");
        Button pauseButton = new Button("Pause");
        Button stopButton = new Button("Stop");

        playButton.setOnAction(event -> mediaPlayer.play());
        pauseButton.setOnAction(event -> mediaPlayer.pause());
        stopButton.setOnAction(event -> mediaPlayer.stop());

        HBox controlBox = new HBox(playButton, pauseButton, stopButton);

       
        VBox vbox = new VBox();
        vbox.getChildren().addAll(mediaView, controlBox);

        
        Scene scene = new Scene(vbox, 400, 300);
        Stage stage = new Stage();
        stage.setTitle("Reproductor de música");
        stage.setScene(scene);
        stage.show();

        // Inicia la reproduccion de la musica
        mediaPlayer.play();
    }
     */
}
