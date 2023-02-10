package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	
	/*Criei a dependência e injetei usando o padrão Factory*/
	private SellerDao dao = DaoFactory.createSellerDao();
	
	public List<Seller> findAll() {
		return dao.findAll(); /* Essa linha vai no BD e vai buscar todos os sellers para mim*/
	}
	
	public void saveOrUpdate(Seller obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	/*método para remover um Seller do BD*/
	public void remove(Seller obj) {
		dao.deleteById(obj.getId());
	}
}
