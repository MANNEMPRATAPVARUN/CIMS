package ca.cihi.cims.model.changerequest;

public enum ChangeRequestRealizationStep {
	STEP_1_SYNC_VIEW("Step 1: synchronize view"),
	STEP_2_CHECK_INCOMPLETES("Step 2: check incompletes"),
	STEP_3_CHECK_CONFLICT("Step 3: check conflicts"),
	STEP_4_REALIZING("Step 4: realizing");



	private String stepDescription;

	private ChangeRequestRealizationStep(String stepDescription){
		this.stepDescription = stepDescription;
	}




	public String getStepDescription() {
		return stepDescription;
	}

	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}


}
