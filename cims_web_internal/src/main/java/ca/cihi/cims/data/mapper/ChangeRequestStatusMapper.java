package ca.cihi.cims.data.mapper;

import java.util.Collection;

import ca.cihi.cims.model.changerequest.ChangeRequestStatusIdentifier;

/**
 * Change request status mapper interface
 * @author rshnaper
 *
 */
public interface ChangeRequestStatusMapper {

	/**
	 * Returns all available {@link ChangeRequestStatusIdentifier} objects from the system
	 * @return
	 */
	public Collection<ChangeRequestStatusIdentifier> getChangeRequestStatuses();
}
