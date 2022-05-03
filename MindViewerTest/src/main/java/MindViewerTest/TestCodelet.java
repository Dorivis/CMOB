/**
 * 
 */
package MindViewerTest;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

/**
 * 
 * @author gudwin
 *
 */
public class TestCodelet extends Codelet{
    
        boolean ascending = true;

	public TestCodelet(String name) {
		setName(name);
	}

	@Override
	public void accessMemoryObjects() {
		
	}

	@Override
	public void calculateActivation() {
            double currentactivation = getActivation();
        if (ascending) {
            try {
                setActivation(getActivation()+0.1);
            } catch (CodeletActivationBoundsException e) {
                ascending = !ascending;
            }
        }
        else {
            try {
                setActivation(getActivation()-0.1);
            } catch (CodeletActivationBoundsException e) {
                ascending = !ascending;
            }
        }
	}

	@Override
	public void proc() {
		
	}

}
