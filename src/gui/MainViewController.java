package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;

	@FXML
	private MenuItem menuItemDepartment;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml", /*Função para inicializar o controlador*/
				(/*Peguei a referência para o controller dele*/SellerListController controller) ->{
					/* Para injetar a dependência do service no controller*/controller.setSellerService(new SellerService());
					controller.updateTableView();
				}
		);	}

	/*O segundo argumento é a ação de inicialização do SellerListController*/
	
	@FXML
	public void onMenuItemDepartmentAction() {  
		loadView("/gui/DepartmentList.fxml", /*Função para inicializar o controlador*/
				(/*Aula 317. Peguei a referência para o controller dele*/DepartmentListController controller) ->{
					/* Para injetar a dependência do service no controller*/controller.setDepartmentService(new DepartmentService());
					controller.updateTableView();
				}
		);	
	}

	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", /*Pois a janela "About não possui controlador*/x -> {}); /*Carreguei a scene*/
	}

	/* Função para abrir uma outra tela. Com o <T>, minha função virou genérica */
	private synchronized /*Para garantir que o processamento do meu método ocorra sem ser interrompido*/
	<T> void loadView(String absoluteName, /*Interface funcional*/ Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); /*Isso é padrão para
			instanciar uma nova Scene, o que muda é o que esta dentro de GetResource, que é o caminho para a
			nova tela e se da pelo nome do arquivo xml de absoluteName, ou seja, da página que quero acessar*/
			VBox newVBox = loader.load(); /*Para carregar a minha nova View na VBox, que é
			a base utilizada no SceneBuilder para a minha nova View*/
			
			/*Macete para mostrar minha View dentro da janela principal. Para isso, irei pegar uma referência da
			 * scene principal na MainView, cujo nome aqui no projeto é mainScene*/
			
			Scene mainScene = Main.getMainScene(); /*Peguei a Scene principal de Main.java e instanciei
			em mainScene*/
			
			/*Vou inserir os Children do VBox da minha About.fxml dentro dos Children do meu MainViewfxml.*/
			/*Referência para o VBox da minha janela principal*/
			VBox mainVBox = (VBox) /*Peguei a referência para meu VBox da janela principal fazendo esse casting*/
			((ScrollPane) mainScene.getRoot())/*Irá pegar o primeiro elemento da minha MainView,
			que no caso é o ScroolPane. Fiz o casting para ScrollPane para que meu computador entenda que eu
			estou referenciando o scrollPane do MainScene*/.getContent();/*Criado para eu acessar meu Content
			dentro do scrollPane*/
			
			/*Agora eu tenho que preservar o MenuBar da minha MainView, excluir o restante que estiver dentro de
			 * Children do VBox, incluir o MenuBar novamente e os Children da minha scene About.fxml.*/
			
			/*Guardei a referência para o Menu*/
			Node mainMenu = mainVBox.getChildren().get(0); /*Peguei o primeiro filho da MainBox e guardei em 
			mainMenu, que no caso é meu menubar*/
			mainVBox.getChildren().clear(); /*Limpei todos os filhos do meu VBox*/
			mainVBox.getChildren().add(mainMenu);/*Adicionei ao mainVBox o mainMenu, que contém o mainbar*/
			mainVBox.getChildren().addAll(newVBox.getChildren()); /*Adicionei os filhos do meu newVBox*/
			
			/*Os 2 Comandos necessários para ativar a função que foi meu segundo argumento no meu loadView,
			 * Agora, meu getController vai retornar o controlador do tipo que eu der entrada como argumento*/
			T controller = loader.getController();
			/*Vou executar a ação initializingAction*/
			initializingAction.accept(controller);
			
		} catch (IOException e) {
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}

	}			

	@Override
	public void initialize(URL uri, ResourceBundle rb) {

	}

}
