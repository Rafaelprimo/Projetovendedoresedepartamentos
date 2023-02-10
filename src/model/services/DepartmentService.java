package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	/*Criei a dependência e injetei usando o padrão Factory*/
	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	
	/*Para testar, eu MOCKEI os dados, ou seja, criei dados fictícios sem acessar o BD.*/
	public List<Department> findAll() {
		/*List<Department> list = new ArrayList<>();
		list.add(new Department(1, "book"));
		list.add(new Department(2, "Computers"));
		list.add(new Department(3, "Electronics"));
		return list;*/
		return dao.findAll(); /* Essa linha vai no BD e vai buscar todos os departamentos para mim*/
	}
	
	
	public void saveOrUpdate(Department obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	/*Aula 386. método para remover um Department do BD*/
	public void remove(Department obj) {
		dao.deleteById(obj.getId());
	}
}
