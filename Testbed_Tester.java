package my_tests;

import java.awt.AWTException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main class for the car testing app. The class manages the creation, simulation, 
 * and evaluation of cars across generations. 
 * 
 * @method iterate
 * 	Simulates a car given a seed. 
 * @method calcV
 * 	Used to calculate car's velocity from two pieces of position data. 
 * 
 * @author Andrew M. 
 *
 */
public class Testbed_Tester {

	public static final String GRAVITY_SETTING = "Gravity"; // put our setting key somewhere

	public final int generation_size = 3; //number of cars to make before new generation
	public final int num_generations = 30; //number of generations to run
	ArrayList<Set> this_gen = new ArrayList<Set>(); 


	public static void main(String[] args) throws AWTException, InterruptedException{
		Testbed_Tester tester = new Testbed_Tester(); 

		ArrayList<Object> next = null; // = iterate(null, true); 

		for(int ii = 0; ii < tester.num_generations; ii ++){
			System.out.println();
			System.out.println("********** NEW GENERATION **********      " + ii);
			System.out.println();

			for(int jj = 0; jj < tester.generation_size; jj ++){
				System.gc(); 
				if(ii == 0){ //the first generation
					next = tester.iterate(null, true, ii, jj); //must use null seed
					tester.this_gen.add((Set) next.get(0)); 
				}
				else {
					Set seed = tester.this_gen.get(jj); 
					//seed.evolve(); 
					next = tester.iterate(seed, true, ii, jj); //SHOULD be false (genetic alg done) 
					tester.this_gen.set(jj, (Set) next.get(0)); //update the seed for next generation
				}
			}

			//update fitness
			for(Set s : tester.this_gen){
				s.fit = s.calculateFitness(); 
			}
			//make new seeds for next generation
			for(int jj = 0; jj < tester.this_gen.size(); jj ++){
				tester.this_gen.set(jj, tester.this_gen.get(jj).evolve(tester.this_gen)); 
			}
		}
		System.out.println("Test completed; successful car.");
	}

	Test t; 

	public ArrayList<Object> iterate(Set seed, boolean r, int gen, int n) throws InterruptedException{
		//es.execute(new ThreadA())
		ExecutorService es = Executors.newCachedThreadPool();

		MyCar c = new MyCar(seed, r);  
		if(seed != null)
			c.name = gen + ", " + n + "; " + seed.name; 
		else 
			c.name = gen + ", " + n; 

		System.out.println("Current car: " + c.name);
		t = new Test(c); 

		es.execute(t); 

		ArrayList<Object> fin = new ArrayList<Object>(); 
		Set done = new Set(); 
		boolean succ = false; 

		long start = System.currentTimeMillis(), dur = start+10;
		boolean track = true; 
		boolean iterate = true; 

		double pi = 0, pf = 0; 		
		boolean swap = false; 
		boolean change = true; 
		int endCount = 0; 
		int resetEnd = 0; //resets disable counter 
		int endMax = 30; //how often to reset

		double velocity = 0; 
		//System.out.println("St: " + start);
		//System.out.println("D1: " + dur);
		Thread.sleep(100); 

		while(iterate || dur < 2000) {
			//System.out.println(System.currentTimeMillis());
			dur = System.currentTimeMillis()-start; 
			//System.out.println("dur: " + dur);
			if(change){
				swap = !swap; 
				change = false; 
			}

			if(dur > 1500 && swap) {
				try {
					pi = c.m_wheel.get(0).getPosition().x; 
				} 
				catch(Exception e){
					e.printStackTrace(); 
				}
				//System.out.println(pi);
				change = true; 
			}

			if(dur > 1500 && !swap){
				//lastT = System.currentTimeMillis(); 
				try {
					pf = c.m_wheel.get(0).getPosition().x; 
					change = true; 
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			//System.out.println("Hi"); 
			if(track){
				//System.out.println("D: " + dur);
				//long dT = now - lastT; 
				//System.out.println("Pf: " + pf + " Pi: " + pi);
				velocity = Math.abs(calcV(pi, pf, 1));
				//System.out.println("V: " + velocity);
			}

			Thread.sleep(100);

			//check if the car has failed
			if(((velocity < 1E-4 && pf < 295) || c.m_wheel.get(0).getPosition().y < -50)){
				if(dur > 2500){
					endCount ++; 
					resetEnd = 0; 
					//iterate = false; //(break)
				}
			}
			else if (pf > 295){
				System.out.println("CAR VICTORY");
				succ = true; //car succeeded 
				iterate = false; 
			}
			if(endCount > 15){
				iterate = false; 
			}
			if(resetEnd > endMax){
				endCount = 0; 
			}
			if(resetEnd < endMax+1)
				resetEnd ++; 
		}
		//put quantities into the done set of info 
		while(true){
			try {
				done.dist = c.m_wheel.get(0).getPosition().x; 
				break; 
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
		System.out.println("Car Failure, making new seed bc V=" + velocity + " and P=" + c.m_wheel.get(0).getPosition().x);
		done.a = c.a; 
		done.b = c.b; 
		done.wj = c.wj; 
		done.cr = c.cr; 
		done.wd = c.wd; 
		done.wNum = c.wNum; 
		done.dist = c.m_wheel.get(0).getPosition().x; 
		done.ws = c.ws; 
		done.name = c.name; 

		fin.add(done); 
		fin.add(succ); 

		//test is over; end the test.
		t.panel.setEnabled(false);
		t.panel.setVisible(false);
		t.testbed.setVisible(false);

		System.out.println();
		es.shutdownNow();
		es.shutdown();
		return fin;
	}

	public static double calcV(double pi, double pf, double t){
		return (pf-pi)/t; 
	}
}
