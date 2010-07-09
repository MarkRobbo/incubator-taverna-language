/**
 * 
 */
package net.sf.taverna.scufl2.api.activity;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.taverna.scufl2.api.common.AbstractNamed;
import net.sf.taverna.scufl2.api.common.Child;

/**
 * An InputActivityPort represents an input to an Activity.
 * 
 * Note that InputActivityPort is not part of the Port hierarchy as it does not
 * represent a workflow object but is instead a means of passing data to
 * whatever underlies the Activity.
 * 
 * @author alanrw
 * 
 */
public class InputActivityPort extends AbstractNamed implements Child<Activity> {

	private Activity parent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.taverna.scufl2.api.common.Child#getParent()
	 */
	@XmlTransient
	public Activity getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.taverna.scufl2.api.common.Child#setParent(net.sf.taverna.scufl2
	 * .api.common.WorkflowBean)
	 */
	public void setParent(Activity parent) {
		this.parent = parent;
	}

}