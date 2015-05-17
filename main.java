import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;


import java.awt.*;
import java.util.concurrent.TimeUnit;
 
@ScriptManifest(author = "Vale and Datosh", info = "Basic Combat Bot", name = "Vatosh Combat Bot", version = 0.53, logo = "")
public class main extends Script {
       
	private final boolean DEBUG = true;
        private State currentState = State.IDLE;
        private long timeBegan, timeRan;
        private long totalBeginningXP, totalCurrentXP, totalXPGained;
        private long beginningAttackXP, currentAttackXP, attackXPGained;
        private long beginningDefenceXP, currentDefenceXP, defenceXPGained;
        private long beginningStrengthXP, currentStrengthXP, strengthXPGained;
        private long beginningHitpointsXP, currentHitpointsXP, hitpointsXPGained;
        private long beginningPrayerXP, currentPrayerXP, prayerXPGained;
        private NPC target;
        //private GroundItem feather;
       
       // Begin program and record initial data.
        @Override
        public void onStart() {
                log("Let's get started!");
                timeBegan = System.currentTimeMillis(); // Begin recording bot runtime.
                beginningAttackXP = skills.getExperience(Skill.ATTACK);
                beginningDefenceXP = skills.getExperience(Skill.DEFENCE);
                beginningStrengthXP = skills.getExperience(Skill.STRENGTH);
                beginningHitpointsXP = skills.getExperience(Skill.HITPOINTS);
                beginningPrayerXP = skills.getExperience(Skill.PRAYER);
                totalBeginningXP = beginningAttackXP + beginningDefenceXP + beginningStrengthXP + beginningHitpointsXP + beginningPrayerXP;
        }
    
        /*
        * Currently not working.
        public void antiBan() throws InterruptedException {
        	if (random(0,30) == 1) {
        		switch(random(0, 7)) {
        		case 0:
        			this.client.rotateCameraPitch(gRandom(55,5));
        			log("Rotating camera (AntiBan)");
        			break;
        		case 1:
        			this.openTab(Tab.SKILLS);
        			log("Checking other tabs (AntiBan)");
        			break;
        		case 2:
        			this.openTab(Tab.FRIENDS);
        			log("Checking other tabs (AntiBan)");
        			break;
        		case 3:
        			this.client.rotateCameraToAngle(140 + random(25 + 80));
        			log("Rotating camera (AntiBan)");
        			break;
        		case 4:
        			this.client.rotateCameraPitch(gRandom(50, 30));
        			log("Rotating camera (AntiBan)");
        			break;
        		case 5:
        			this.client.openTab(Tab.INVENTORY);
        			log("Checking other tabs (AntiBan)");
        			break;
        		case 6:
        			this.client.moveMouseOutsideScreen();
        			log("Moving mouse outside screen (AntiBan)");
        			sleep(random(5000, 10000));
        		}
        	}
        }
        */
        
        @Override
        public int onLoop() throws InterruptedException {
               
        	/* Currently not working.
	        	if (myPlayer().isAnimating()) {
					antiBan();
				}
			*/
        	
                switch (currentState) {
                case IDLE:
                        // Try to get the next target. If one is found switch to the fighting
                        // state. If none found sleep and try again next loop.
                		target = npcs.closest("Barbarian"); // Change to user-input (So they can enter Goblin, cow, etc.)
                        if(target != null) {
                                currentState = State.FIGHT;
                        } else {
                                sleep(random(300, 600));
                        }
                       
                        break;
                case FIGHT:
                        // Check that target is still a valid object.
                        if(target == null) {
                                currentState = State.IDLE;
                        }
                       
                        // Attack or turn camera
                        if(!myPlayer().isAnimating() && !myPlayer().isMoving() && !target.isUnderAttack()) {
                        	target.interact("Attack");
                                sleep(random(1300, 1800));
                        } else {
                                camera.toEntity(target);
                        }
                       
                        if(target.getHealth() == 0) {
                                target = null;
                                currentState = State.LOOT;
                        }
                        break;
                // Make loot a togglable option.
                case LOOT:
                	
                    if(DEBUG) {
                            log("Entered LOOT");
                    }
                   
                    GroundItem groundBones = groundItems.closest("Bones");
        	    Item bones = inventory.getItem("Bones");
        	    if ((!inventory.isFull() && !myPlayer().isUnderAttack())) {
        	    	groundBones.interact("Take");
        		sleep(random(800, 1200));
        		bones.interact("Bury");
        		currentState = State.IDLE;
        		} else {
        			log("Full inventory. Exiting program.");
                            	onExit();
                    }
        	    /*
        	    * Add this feature for any user input, not just feathers.
                    // Check for near feathers
                    feather = groundItems.closest("Feather");
                    if(!inventory.isFull()) {
                            feather.interact("Take");
                            sleep(random(100, 200));
                    } else {
                    	    log("Full inventory. Exiting program.");
                            onExit();
                    }
                    */
                    break;
                }
		return random(200, 300);
        }
       
        @Override
        public void onExit() {
                log("Thanks for running our script!");
        }
       
       // Paint graphical representation of data for the user.
        @Override
        public void onPaint(Graphics2D g) {
        	//Loading paint
    		super.onPaint(g);
    		timeRan = System.currentTimeMillis() - this.timeBegan;
    		currentAttackXP = skills.getExperience(Skill.ATTACK);
    		currentDefenceXP = skills.getExperience(Skill.DEFENCE);
    		currentStrengthXP = skills.getExperience(Skill.STRENGTH);
    		currentHitpointsXP = skills.getExperience(Skill.HITPOINTS);
    		currentPrayerXP = skills.getExperience(Skill.PRAYER);
    		totalCurrentXP = currentAttackXP + currentDefenceXP + currentStrengthXP + currentHitpointsXP + currentPrayerXP;
    		
    		attackXPGained = currentAttackXP - beginningAttackXP;
    		defenceXPGained = currentDefenceXP - beginningDefenceXP;
    		strengthXPGained = currentStrengthXP - beginningStrengthXP;
    		hitpointsXPGained = currentHitpointsXP - beginningHitpointsXP;
    		prayerXPGained = currentPrayerXP - beginningPrayerXP;
    		totalXPGained = totalCurrentXP - totalBeginningXP;
    		
    		g.setColor(Color.GREEN);
    		g.drawString("Vatosh Combat Bot v0.53", 5, 290);
    		g.setColor(Color.RED);
    		g.drawString("Runtime: " + ft(timeRan), 5, 304);
    		g.drawString("Total XP Earned: " + totalXPGained, 5, 318);
    		
    		g.setColor(Color.YELLOW);
    		g.drawString("Attack XP Earned: " + attackXPGained, 320, 20);
    		g.drawString("Defence XP Earned: " + defenceXPGained, 320, 34);
    		g.drawString("Strength XP Earned: " + strengthXPGained, 320, 48);
    		g.drawString("Hitpoints XP Earned: " + hitpointsXPGained, 320, 62);
    		g.drawString("Prayer XP Earned: " + prayerXPGained, 320, 76);
        }
        
        private String ft(long duration)
		{
        	// Used to convert the runtime of the bot to a readable amount.
			String res = "";
			long days = TimeUnit.MILLISECONDS.toDays(duration);
			long hours = TimeUnit.MILLISECONDS.toHours(duration)
			- TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
			long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
			- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
			.toHours(duration));
			long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
			- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
			.toMinutes(duration));
			if (days == 0) {
			res = (hours + ":" + minutes + ":" + seconds);
			} else {
			res = (days + ":" + hours + ":" + minutes + ":" + seconds);
			}
			return res;
		} 
       
        private enum State {
                FIGHT, LOOT, IDLE,
        }
}
