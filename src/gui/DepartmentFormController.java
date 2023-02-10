package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exception.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	/*Dependência para o Department*/
	private Department entity;
	
	/*Dependência para o Department*/
	private DepartmentService service;
	
	/*Aula 383, padrão Observer*/
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>(); /*Permite com que outros objetos se
	inscrevam nesta lista e recebam o evento gerado por ele*/
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;	
	
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
		Department para mim.*/
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

	/*Pega os dados fo formulário e me retorna um obj Department*/
	/*Aula 384. Para realizar a validação de dados, implementei apenas que o campo nome não pode ser vazio*/
	private Department getFormData() {
		Department obj = new Department();
		ValidationException exception = new ValidationException("Validation Error"); /*Instanciei a exceção*/
		obj.setId(Utils.tryParseToInt(txtId.getText())); /*Transformeu String para Inteiro*/
		/*Aula 384*/
		if (txtName.getText() == null || /*O trim() Irá eliminar os espaços em branco antes e no final*/
				txtName.getText().trim().equals("") /*Significa que minha caixa esta vazia*/) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());
		
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
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
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
		Constraints.setTextFieldInteger(txtId); /*Para aceitar apenas números inteiros no Id*/
		Constraints.setTextFieldMaxLength(txtName, 30);;
	}
	
	/*Responsável por pegar "entity" e popular as caixas de texto do formulário*/
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId())); /*Converti o inteiro "Id" para String*/
		txtName.setText(entity.getName());
	}
	/*Aula 384*/
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet(); /*Coleção com o nome dos campos que deram erro*/
		/*Irei percorrer o conjunto e fazer uma verificação de 1 por 1*/
		/*Se meu Set contém algum valor "name"*/ if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name")); /*Pego a Msg referente ao campo "name" e a coloco no
			meu labelErrorName*/
		}
	}
	
}
