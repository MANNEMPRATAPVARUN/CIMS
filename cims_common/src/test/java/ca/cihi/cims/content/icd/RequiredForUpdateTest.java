package ca.cihi.cims.content.icd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.dal.annotations.RequiredForUpdate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/applicationContext-test.xml" })
public class RequiredForUpdateTest {


	@Test
	public void testTextPropertyVersionFields() {
	
		Field[] fields = TextPropertyVersion.class.getDeclaredFields();
		
		for (Field field : fields) {
			System.out.println(field.getName());
			if (field.isAnnotationPresent(RequiredForUpdate.class)) {
				System.out.println("Found the annotation");
			}
		}		
	}
	
	@Test
	public void testAnnotationRetrieval() {
	
		Iterable<Field> i = getFieldsUpTo(TextPropertyVersion.class);		
		Iterator<Field> iFields = i.iterator();
		
		while (iFields.hasNext()) {
			Field field = iFields.next();
			//System.out.println(field.getName());
			if (field.isAnnotationPresent(RequiredForUpdate.class)) {
				System.out.println(field.getName());				
			}
		}		
	}
	
	private Iterable<Field> getFieldsUpTo(Class<?> startClass) {

		List<Field> currentClassFields = new ArrayList<>(Arrays.asList(startClass.getDeclaredFields()));
		if (currentClassFields == null){
			currentClassFields = new ArrayList<>();
		}

		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && !(parentClass.equals(java.lang.Object.class))) {
			List<Field> parentClassFields = (List<Field>) getFieldsUpTo(parentClass);
			if (parentClassFields != null && !parentClassFields.isEmpty()){
				currentClassFields.addAll(parentClassFields);
			}
		}

		return currentClassFields;
	}
	

}
