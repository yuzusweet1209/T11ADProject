package sg.edu.iss.ad.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.iss.ad.DTO.UserStockWatchListDTO;
import sg.edu.iss.ad.model.Stock;
import sg.edu.iss.ad.model.UserStockWatchList;
import sg.edu.iss.ad.repository.StockRepository;
import sg.edu.iss.ad.repository.UserRepository;
import sg.edu.iss.ad.repository.UserStockWatchListRepository;

@Service
public class UserStockWatchListService {
	@Autowired
	UserStockWatchListRepository uwlrepo;
	
	@Autowired
	StockRepository srepo;
	
	@Autowired
	UserRepository urepo;
	
	//convert to DTO to hide confidential user details
	public List<UserStockWatchListDTO> getuserstockwatchlist(String username){
		List<UserStockWatchList> userwatchlist= uwlrepo.GetWatchList(username);
		List<UserStockWatchListDTO> userwatchlistDTO=new ArrayList<UserStockWatchListDTO>();
		for(UserStockWatchList stock:userwatchlist) {
			UserStockWatchListDTO stockDTO=new UserStockWatchListDTO();
			stockDTO.setStockticker(stock.getStock().getStockTicker());
			stockDTO.setUsername(stock.getUser().getUsername());
			userwatchlistDTO.add(stockDTO);
		}
		return userwatchlistDTO;
	}
	
	//convert to DTO before saving
	@Transactional
	public void addstocktowatchlist(UserStockWatchListDTO newstock) {
		UserStockWatchList stock=new UserStockWatchList();
		
		//check if newstock added is existing, if not add
		if(srepo.findByStockTicker(newstock.getStockticker())==null) {
			Stock stocktoadd=new Stock();
			stocktoadd.setStockTicker(newstock.getStockticker());
			stocktoadd.setStockName(newstock.getStockticker());
			srepo.save(stocktoadd);
		}
		
		stock.setStock(srepo.findByStockTicker(newstock.getStockticker()));
		stock.setUser(urepo.findByUsername(newstock.getUsername()));
		uwlrepo.save(stock);
	}
	
	@Transactional
	public void deletestockfromwatchlist(UserStockWatchListDTO deletestock) {
		UserStockWatchList stocktodelete=uwlrepo.FindStockByUserandTicker(deletestock.getUsername(), deletestock.getStockticker());
		uwlrepo.delete(stocktodelete);
	}
}
