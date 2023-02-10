package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	/*Para guardar a referência para minha scene principal*/
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml")); /* Essa instanciação
é importante para eu poder manipular a tela antes de carregá-la. Defini aqui também a minha tela(scene) principal.*/
			ScrollPane/*Parent*/ scrollPane = loader.load(); /*Para carregar a MainView na ScrollPane, que é a base
			utilizada no SceneBuilder para a ManiView*/
			
			/* 2 Comandos para deixar meu ScrollPane ajustado a minha janela*/
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			mainScene = new Scene(scrollPane); /*Scene que será a minha principal sendo instanciada e
			recebendo como argumento minha MainView carregada*/
			primaryStage.setScene(mainScene); /*Seto a scene do meu Stage com essa minha scene principal*/
			primaryStage.setTitle("Sample JavaFX application"); /*Título do meu palco*/
			primaryStage.show(); /*Mostrei o palco*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*Método para pegar o atributo scene*/
	public static Scene getMainScene() {
		return mainScene;
	}
	

	public static void main(String[] args) {
		launch(args);
	}
}