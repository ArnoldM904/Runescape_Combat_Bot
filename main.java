import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
 
import java.awt.*;
 
@ScriptManifest(author = "Vale and Datosh", info = "Basic Combat Bot", name = "Vatosh Combat Bot", version = 0.3, logo = "")
public class main extends Script {
       
		private final boolean DEBUG = true;
        private State currentState = State.IDLE;
        private NPC target;
        private GroundItem bones, feather;
        int killCount = 0;
       
        @Override
        public void onStart() {
                log("Let's get started!");
        }
       
        @Override
        public int onLoop() throws InterruptedException {
               
                switch (currentState) {
                case IDLE:
                        // Try to get the next chicken. If we found one switch to the fighting
                        // state. If we don't sleep and try on next loop again
                		target = npcs.closest("Chicken"); // Change "Chicken" to user-input (So they can enter Goblin, cow, etc.)
                        if(target != null) {
                                currentState = State.FIGHT;
                        } else {
                                sleep(random(300, 600));
                        }
                       
                        break;
                case FIGHT:
 
                        // Do we still have a valid chicken object?
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
                                killCount += 1;
                                //log("You have killed: <killCount> targets.");
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
                    
                    // Go back to idle
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
    		g.setColor(Color.GREEN);
    		g.drawString("Vatosh Combat Bot v0.3", 5, 290);
        }
       
        private enum State {
                FIGHT, LOOT, BURY, IDLE, CLEAR_INVENTORY,
        }
}
