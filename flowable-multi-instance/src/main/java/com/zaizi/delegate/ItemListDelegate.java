package com.zaizi.delegate;

import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class ItemListDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		// TODO Auto-generated method stub

		List<Object> itemList = getArrayList();
		execution.setVariable("itemList", itemList);
	}

	List<Object> getArrayList() {
		List<Object> itemList = new ArrayList<>();
		itemList.add("item 1");
		itemList.add("item 2");

		return itemList;
	}

}
