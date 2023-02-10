package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {
	Scanner sc = new Scanner(System.in);
	
	/*Aula 376*/
	/*Dependência para o DepartmentService*/
	private DepartmentService service;
	
	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId; /* O primeiro é o tipo da entidade, no caso é
	Department, o segundo é o tipo da coluna, que depende dela*/															

	@FXML
	private TableColumn<Department, String> tableColumnName; /* O primeiro é o tipo da entidade, no caso é
	Department, o segundo é o tipo da coluna, que depende dela*/
	
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;
	
	/*Aula 386*/
	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;
	
	@FXML
	private Button btNew;
	
	/*Aula 376. Irei carregar os departamentos nessa ObservableList*/
	private ObservableList<Department> obsList;
	
	/* Método para tratamento de eventos ao clicar no botão */
	/*Aula 379, 381*/
	@FXML
	public void onBtNewAction(/*Para que eu tenha uma referência para o controle que recebeu
	o evento*/ActionEvent event) {
		Stage parentStage = Utils.currentStage(event); /*Peguei a referência para o Stage atual*/
		Department obj = new Department(); /*ois o formulário começará vazio*/
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage); /*Passei a referência para cirar minha janela
		de formulário*/
	}

	 /* O simples fato de eu declarar as colunas acima como atributos, não é
	 * suficiente para elas funcionarem, logo, tenho que fazer o que está baixo
	 * dentro do initialize e do método initializeNodes()*/
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes(); /* Método auxiliar */
	}

	private void initializeNodes() {
		/* Comando para iniciar apropriadamente as colunas da minha tabela */
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id")); 
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name")); /*O "id" e o "name" são
		o nome dos atributos na classe Department*/

		/* Macete para a TableView acompanhar a largura e altera da minha janela. */
		/* Referência para o Stage atual */
		Stage stage = (Stage) Main.getMainScene()/* Acessei a scene */.getWindow(); /* Acessei o Stage.
		O window é super classe do Stage*/
		/*Comando para a tableViewDepartment acompanhar a janela */
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); /* No bind, chamei o Height
		property do Stage */
	}
	
	/*Aula 376*/
	/*Dependência do DepartmentService aqui na classe Resource(Controller) é necessária e está lá em cima,
	 * junto com o setDepartmentService.Carreguei os departamentos que DepartmentService fornece, e
	 * irei mostrá-los na minha TableView*/
	
	/*Aula 376*/
	/*Injeção de dependência*/
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	/*Aula 376. Vai carregar os departamentos, atualizá-los e jogar dentro da ObservableList. Ai irei associar 
	 * a ObservableList com o TableView e os departamentos irão aparecer na tela.*/
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = service.findAll(); /*Irá inserir nesta list os department vindos
		de DepartmentService*/
		/*Carreguei minha list dentro do ObservableList*/
		obsList = FXCollections.observableArrayList(list);
		/*Carreguei na TableView os departamentos vindos de obsList, que vieram de list, que vieram do
		 * DepartmentService, que buscou no BD*/
		tableViewDepartment.setItems(obsList);
		/*Aula 385*/
		initEditButtons(); /*Irá acrescentar um botão "edit" em cada linha da minha tabela, e cada botão desse
		, ao ser clicado irá abrir o formulário de edição*/
		initRemoveButtons(); /*Irá acrescentar um botão "Remove" em cada linha da minha tabela, e cada botão desse
		, ao ser clicado irá perguntar se confirmo ou não a deleção.*/
	}
	
	/*Aula 379. Função para carregar a janela do meu formulário para preencher um novo departamento*/
	private void createDialogForm(/*Aula 381 Irá injetar o controlador na minha tela de formulário, olhar abaixo*/
			Department obj,
			/*Nome da view que quero carregar*/String absoluteName,
			/*Referência para meu Stage que o dialog vai ficar pendurado*/Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			/*Aula 381, peguei uma referência para o controlador*/
			DepartmentFormController controller = loader.getController(); /*Peguei o controlador da tela que 
			carreguei acima*/
			controller.setDepartmentService(new DepartmentService()); /*Injetei o DepartmentService*/
			controller.setDepartment(obj); /*Injetei o departamento no controlador*/
			controller.subscribeDataChangeListener(this);/*inscrição para ouvir o OnDataChanged()*/ /*O this
			significa que é o objeto deste DepartmentListController*/
			controller.updateFormData(); /*Carregará os dados do meu objeto acima no meu formulário*/
			
			
			/*Para carregar o dialog em frente a outra janela, preciso instanciar um novo Stage*/
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data"); /*Configurei o título do meu Stage*/
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false); /*Diz se minha janela pode ou não ser redimencionada*/
			dialogStage.initOwner(parentStage);  /*o parentStage é o pai da minha Dialog, por isso está ai*/
			dialogStage.initModality(Modality.WINDOW_MODAL); /*Diz se minha janela vai ser modal ou se
			terá outro comportamento. Da forma que está, enquanto eu não fechar a janela dialog, eu não poderei
			acessar a anterior ou de trás*/
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	/*Quando disparar este evento, eu vou precisar atualizar minha tela*/
	@Override
	public void onDataChanged() {
		updateTableView();
	}
	
	/*Código muito específico do Framework que cria um objeto CellFactory responsável por instanciar os botões
	 * e configurar o evento do botão. Estou criando minha tela de preenchimento com meu objeto já preenchido
	 */
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");
			@Override /*O obj é o department da linha que tiver o o botão de edição que eu clicar*/
			protected void updateItem(Department obj, boolean empty) {
					super.updateItem(obj, empty);
					if (obj == null) {
						setGraphic(null);
						return;
					}
					setGraphic(button);
					button.setOnAction(
							event -> createDialogForm( /*Método para criar a janela do formulário*/
									obj, "/gui/DepartmentForm.fxml",Utils.currentStage(event)));
			}
		});
	}
	
	/*Aula 386. Vou chamá-lo dentro do updateTableView*/
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
			});
	}
	
	/*Operação para remover uma entidade*/
	private void removeEntity(Department obj) {
		/*A resposta do Alert ficará na variável result*/
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		if (result.get() == ButtonType.OK) /*Se o botão "ok" foi apertado, a deleção foi confirmada*/ {
			/*Programação defensiva, se service == null, não foi injetada a dependência*/
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView(); /*Para forçar a atualização dos dados da tabela*/
			}
			/*Mesma exceção do DepartmentDaoJDBC*/
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
	}
	
}
