package ca.cihi.cims.model;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * The SpelModel is a simple way to wrap an ugly underlying model with a bean
 * facade, using SPEL expressions.
 * @see SpelModelTest
 */
public class SpelModel {
	private Object basis;

	public SpelModel(Object basis) {
		this.basis = basis;
	}

	@SuppressWarnings("unchecked")
	protected <T> T read(String spelExpression) {
		ExpressionParser parser = new SpelExpressionParser();
		return (T) parser.parseExpression(spelExpression).getValue(basis);
	}
}
