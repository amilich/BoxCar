package my_tests;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Robot;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jbox2d.testbed.framework.TestbedController;
import org.jbox2d.testbed.framework.TestbedController.MouseBehavior;
import org.jbox2d.testbed.framework.TestbedController.UpdateBehavior;
import org.jbox2d.testbed.framework.TestbedErrorHandler;
import org.jbox2d.testbed.framework.TestbedModel;
import org.jbox2d.testbed.framework.TestbedTest;
import org.jbox2d.testbed.framework.j2d.DebugDrawJ2D;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;

import com.sun.glass.events.KeyEvent;


public class Test implements Runnable {
	TestbedController controller;
	TestbedModel model = new TestbedModel();         // create our model
	TestPanelJ2D panel;
	JFrame testbed = new JFrame(); // put both into our testbed frame

	public Test(TestbedTest c){
		model.addCategory("My Tests");
		model.addTest(c);
		controller =
				new TestbedController(model, UpdateBehavior.UPDATE_CALLED, MouseBehavior.NORMAL,
						new TestbedErrorHandler() {
					@Override
					public void serializationError(Exception e, String message) {
						JOptionPane.showMessageDialog(null, message, "Serialization Error",
								JOptionPane.ERROR_MESSAGE);
					}
				});

		panel = new TestPanelJ2D(model, controller);
		model.setPanel(panel);
		model.setDebugDraw(new DebugDrawJ2D(panel, true));
		testbed.setTitle("JBox2D Testbed");
		testbed.setLayout(new BorderLayout());
		testbed.add((Component) panel, "Center");
		testbed.setSize(100, 100);
		testbed.pack();
		testbed.setVisible(true);
		testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.out.println(System.getProperty("java.home"));
	}

	Robot r;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			r = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		controller.playTest(0);
		controller.start();
		r.keyPress(KeyEvent.VK_D);
		System.gc(); 
		//r.keyPress(KeyEvent.VK_D);
		//System.out.println(c.getPos().getPosition().x);
	}
}
