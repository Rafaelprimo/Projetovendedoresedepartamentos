package model.exception;

import java.util.HashMap;
import java.util.Map;

/*Como ela é para validar meu formulário, irei fazer com que ela carregue as msg de erro do meu formulário, caso
 * existam*/
public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	/*Para carregar os erros do formulário na minha exceção*/
	/*Vou guardar os erros de cada campo do formulário. o primeiro String é o nome da caixa, e o segundo a msg
	 * de erro*/
	private Map<String, String> errors = new HashMap<>();
	
	public ValidationException(String msg) {
		super(msg);
	}
	
	public Map<String, String> getErrors(){
		return errors;
	}
	
	/*Método para adcionar um elemento na coleção*/
	public void addError(String fieldName, String errorMessage){
		errors.put(fieldName, errorMessage);
	}
}
