package my_tests;

import java.util.ArrayList;
import java.util.Random;

public class Set {
	ArrayList<Integer> properties = new ArrayList<Integer>(); 
	ArrayList<Wheel> ws; 

	public float a, b, wj, cr, wd, wh; 
	public int wNum; //number of wheels
	public float dist; 
	public String name = ""; 
	//a, b are physical parameters; wj is wheel joint size; cr is circle radius size
	//wd is wheel distanced

	public Set(){
		//constructor
	}

	private int accepted = 2; //accept top two, use them for next gen
	public int fit = 0; //fitness

	/**
	 * Given a set of the seeds of a particular generation, generate a seed 
	 * for a next generation of car using genetic algorithm.
	 * 
	 * @param this_gen
	 * 	ArrayList of sets of seeds from this gen. 
	 * 
	 * @author Andrew M. 
	 * 
	 */
	public Set evolve(ArrayList<Set> this_gen){
		//calculate fitness 
		fit = calculateFitness(); 
		ArrayList<Set> top_two = getTop(this_gen, accepted); 
		Set newSet = makeCombo(top_two); //TODO: FIND why top two is null
		return newSet; 
	}
	
	/**
	 * Make a combination of a given number of sets (cross over). 
	 * 
	 * @param sets
	 * 	ArrayList of sets from this gen.  
	 * 
	 * @author Andrew M. 
	 * 
	 */
	private Set makeCombo(ArrayList<Set> sets){
		Set temp = new Set(); 
		//grouped
		temp.a = sets.get(0).a; 
		temp.b = sets.get(0).b; 
		//grouped
		temp.wj = sets.get(0).wj; 
		temp.cr = sets.get(0).cr; 
		temp.wd = sets.get(0).wd; 
		//these two should be grouped
		temp.wNum = sets.get(0).wNum; 
		temp.ws = sets.get(0).ws; 
		temp.name += sets.get(0).name; 

		for(int ii = 0; ii < sets.size()-1; ii ++){
			//starting out: just swap wj, cr, wd
			sets.get(ii).wj = sets.get(ii+1).wj; 
			sets.get(ii).cr = sets.get(ii+1).cr; 
			sets.get(ii).wd = sets.get(ii+1).wd; 
		}
		int i = sets.size()-1; //index for final swap
		sets.get(i).wj = temp.wj; 
		sets.get(i).wj = temp.wj; 
		sets.get(i).wd = temp.wd; 
		sets.get(i).name += temp.name; 
		//mutation! 
		Random r = new Random(); 
		for(Set k : sets){
			if(true){ //always have mutations?
				System.out.println("MUTATION");
				//implement
				int ind = r.nextInt(6); 
				if(ind == 0){
					System.out.println("Mutate a");
					k.a *= (0.5+r.nextDouble()); 
				}
				else if (ind == 1){
					System.out.println("Mutate b");
					k.b *= (0.5+r.nextDouble()); 
				}
				else if (ind == 2){
					System.out.println("Mutate wj");
					k.wj *= (0.5+r.nextDouble()); 
				}
				else if (ind == 3){
					System.out.println("Mutate cr");
					k.cr *= (0.5+r.nextDouble()); 
				}
				else if (ind == 4){
					System.out.println("Mutate wd");
					k.wd *= (0.5+r.nextDouble()); 
				}
				else if (ind == 5){
					System.out.println("Mutate wNum");
					if(r.nextBoolean()){
						wNum ++; 

						float x = wd*sign(); //create the new wheel
						float y = -1; 
						while(y < 0)
							y = b-wh*2*sign();//wh*sign();
						ws.add(new Wheel(cr, x, y));
					}
					else {
						wNum --; 
						ws.remove(ws.size()-1); 
					}
				}
			}
		}
		System.gc(); 

		return sets.get(r.nextInt(sets.size())); 
	}

	public ArrayList<Set> getTop(ArrayList<Set> prev, int acceptNum){
		ArrayList<Set> ret = new ArrayList<Set>(); 
		if(prev.size() >= 2){
			ret.add(prev.get(0)); 
			ret.add(prev.get(1));
		}
		for(int ii = 2; ii < prev.size(); ii ++){ //MODITY FOR MORE ACCEPTED
			if(prev.get(ii).fit > ret.get(0).fit)
				ret.set(0, prev.get(ii)); 
			else if(prev.get(ii).fit > ret.get(1).fit)
				ret.set(1, prev.get(ii)); 
		}
		return ret; 
	}

	public int sign(){
		Random r = new Random(); 
		if(r.nextBoolean())
			return -1; 
		return 1; 
	}

	int calculateFitness(){
		return (int) dist/300; 
	}

	public Set(MyCar c){
		this.a = c.a; 
		this.b = c.b; 
		this.wj = c.wj; 
		this.cr = c.cr; 
		this.wd = c.wd; 
		this.wNum = c.wNum; 
		this.ws = c.ws; 
		this.name = c.name; 
	}

	public Set(ArrayList<Wheel> ws){
		this.ws = ws; 
	}

	public String toString(){
		return "Set: a=" + a + "; b=" + b + "; wj=" + wj + "; cr=" + cr + "; wd=" + wd + "; wNum=" + wNum + "; dist=" + dist;
	}
}
