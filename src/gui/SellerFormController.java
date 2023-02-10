package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	
	/*Dependência para o Seller*/
	private Seller entity;
	
	/*Dependência para o Seller*/
	private SellerService service;
	
	/*Aula 392. Dependência para o DepartmentService para que eu possa buscar os Departments no BD*/
	private DepartmentService departmentService;
	
	/*Aula 383, padrão Observer*/
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>(); /*Permite com que outros objetos se
	inscrevam nesta lista e recebam o evento gerado por ele*/
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
		entity = getFormData(); /*Método que irá pegar os dados das caixas e instanciar um objeto
		Seller para mim.*/
		service.saveOrUpdate(entity); /*Salvei no BD*/
		/*Aula 383. Após o salvamento com sucesso, devo notificar os listeners da minha list DataChangeListeners*/
		notifyDataChangeListeners();		
		Utils.currentStage(event).close();/*Irá fechar a janela*/
		}
		/*Aula 384*/
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	/*Aula 383, notificar os listeners vai ser executar o método onDataChanged() da interface DataChangeListener
	 * em cada um dos listeners. Vou emitir o evento onDataChanged() para meus listeners*/
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	/*Pega os dados do formulário e carrega um obj Seller com esses dados*/
	/*Aula 384. Para realizar a validação de dados, implementei apenas que o campo nome não pode ser vazio*/
	private Seller getFormData() {
		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation Error"); /*Instanciei a exceção*/
		obj.setId(Utils.tryParseToInt(txtId.getText())); /*Transformeu String para Inteiro*/
		
		/*Aula 384*/
		if (txtName.getText() == null || /*O trim() Irá eliminar os espaços em branco antes e no final*/
				txtName.getText().trim().equals("") /*Significa que minha caixa esta vazia*/) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());
		
		/*Aula 392*/
		if (txtEmail.getText() == null || /*O trim() Irá eliminar os espaços em branco antes e no final*/
				txtEmail.getText().trim().equals("") /*Significa que minha caixa esta vazia*/) {
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());
		
		/*Aula 393*. Irá pegar o valor do DatePicker e instanciar na minha variável*/
		if(dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		}
		else {
		Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));/*Vai
		converter o valor do DatePicker para meu Instant que é uma data independente de localidade*/
		/*O setBirthDate do meu objeto espera um dado do tipo Date, logo, terei que transformar o Instant
		 * para Date*/
		obj.setBirthDate(Date.from(instant));
		}
		
		/*Aula 393*/
		if (txtBaseSalary.getText() == null || /*O trim() Irá eliminar os espaços em branco antes e no final*/
				txtBaseSalary.getText().trim().equals("") /*Significa que minha caixa esta vazia*/) {
			exception.addError("baseSalary", "Field can't be empty");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue());
		
		/*Aula 384. Caso exista algum erro, ela será lançada*/
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();/*Irá fechar a janela*/
	}	
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	/* Injetei as duas dependências de uma vez*/
	public void setServices(SellerService service, DepartmentService departmentservice) {
		this.service = service;
		this.departmentService = departmentservice;
	}
	
	/*Aula 383. Método para que outros objetos possam se inscrever na minha DataChangeListener list*/
	/*Outros objetos podem se inscrever para receber o evento desta classe desde que implementem o DataChangeListener*/
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
 	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		/*Limitações impostas a cada TextField*/
		Constraints.setTextFieldInteger(txtId); /*Para aceitar apenas números inteiros no Id*/
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyy"); /*Formatei minha data para que apenas sejam inseridos
		dados no DatePickerpBirthDate no padrão que eu quis. Utilizai o código do método formatDatePicker() de 
		Utils.java para isso*/
		Constraints.setTextFieldDouble(txtBaseSalary);
		initializeComboBoxDepartment();
	}
	
	/*Responsável por pegar "entity" e popular as caixas de texto do formulário*/
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId())); /*Converti o inteiro "Id" para String*/
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		if (entity.getBirthDate() != null) {
		dpBirthDate.setValue(LocalDate.ofInstant(/*Converti minha BirthDate para instante*/
				entity.getBirthDate().toInstant(), /* Irá pegar o fuso da minha máquina*/ZoneId.systemDefault()));
		/*O	BirthDate é tipo Date, o DatePicker trabalha com o local Date logo, fiz o que ta acima para	chamar a
		 * data que ta no banco de dados para meu Date local.*/
		Locale.setDefault(Locale.US); /*PAra garntir que aparecerá o ponto e não a vírgula no BaseSalary*/
		}
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary())); /*Peguei o Double e transformei em
		String com 2 casas após o ponto*/
		/* Se isso acontecer, é pq é um Department novo, logo, vou setar o meu combobox no primeiro Department*/
		if ( entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		}
		else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}
	}
	
	/*Aula 392. Método responsável por carregar meus Department do DepartmentService no ObservableList*/
	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}
		List<Department> list = departmentService.findAll();
		/*Aula de comboBox*/
		obsList = FXCollections.observableArrayList(list);
		/*Irei setar a obsList com o meu comboBox*/
		comboBoxDepartment.setItems(obsList);
	}
	
	
	
	
	/*Aula 384*/
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet(); /*Coleção com o nome dos campos que deram erro*/
		/*Irei percorrer o conjunto e fazer uma verificação de 1 por 1*/
		/*Se meu Set contém algum valor "name"*/ if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name")); /*Pego a Msg referente ao campo "name" e a coloco no
			meu labelErrorName*/
		}
		else {
			labelErrorName.setText("");
		}
		
		if (fields.contains("email")) {
			labelErrorEmail.setText(errors.get("email")); 
		}
		else {
			labelErrorEmail.setText("");
		}
		
		if (fields.contains("baseSalary")) {
			labelErrorBaseSalary.setText(errors.get("baseSalary")); 
		}
		else {
			labelErrorBaseSalary.setText("");
		}
		
		if (fields.contains("birthDate")) {
			labelErrorBirthDate.setText(errors.get("birthDate")); 
		}
		else {
			labelErrorBirthDate.setText("");
		}
	}
	

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}