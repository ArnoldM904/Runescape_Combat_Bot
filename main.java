import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
 

import java.awt.*;
import java.util.concurrent.TimeUnit;
 
@ScriptManifest(author = "Vale and Datosh", info = "Basic Combat Bot", name = "Vatosh Combat Bot", version = 0.5, logo = "")
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
        private GroundItem bones, feather;
       
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
       
        @Override
        public int onLoop() throws InterruptedException {
               
                switch (currentState) {
                case IDLE:
                        // Try to get the next target. If one is found switch to the fighting
                        // state. If none found sleep and try again next loop.
                		target = npcs.closest("Chicken"); // Change "Chicken" to user-input (So they can enter Goblin, cow, etc.)
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
                        if(!myPlayer().isAnimating() && !myPlayer().isMoving()) {
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
                case LOOT:
                	
                    if(DEBUG) {
                            log("Entered LOOT");
                    }
                   
                    // Check for near bones
                    bones = groundItems.closest("Bones");
                    if(!inventory.isFull()) {
                            bones.interact("Take");
                            sleep(random(100, 200));
                    } else {
                            currentState = State.CLEAR_INVENTORY;
                    }
                   
                    // Check for near feathers
                    feather = groundItems.closest("Feather");
                    if(!inventory.isFull()) {
                            feather.interact("Take");
                            sleep(random(100, 200));
                    } else {
                            currentState = State.CLEAR_INVENTORY;
                    }
                    currentState = State.BURY;                   
                    break;
                    
            case CLEAR_INVENTORY:
                   
                    if(DEBUG) {
                            log("Entered CLEAR_INVENTORY");
                    }
                    inventory.dropAllExcept("Bones", "Feather");
                    currentState = State.IDLE;
                    break;
                    
            case BURY:
                
                if(DEBUG) {
                        log("Entered BURY");
                }
               
                Item items[] = this.getInventory().getItems();
                for(Item i : items) {
                       
                        if(i == null) {
                                continue;
                        }
                       
                        if(DEBUG) {
                                log(i.toString() + " " + i.getName());                                 
                        }
                       
                        if(i.getName().equals("Bones")) {
                               
                                if(DEBUG) {
                                        log("try to bury bones");
                                }
                               
                                i.interact("Bury");
                                sleep(random(600, 1000));
                        }
                }
                currentState = State.IDLE;
                break;
 
                default:
                        break;
                }
                return random(200, 300);
        }
       
        @Override
        public void onExit() {
                log("Thanks for running our script!");
        }
       
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
    		g.drawString("Vatosh Combat Bot v0.5", 5, 290);
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
                FIGHT, LOOT, BURY, IDLE, CLEAR_INVENTORY,
        }
}
