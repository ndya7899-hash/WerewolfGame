import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WerewolfGame {
    private Player[] players;
    private Scanner scanner = new Scanner(System.in);
    private int killedByWolf = -1;
    private int wolfIdx, witchIdx, hunterIdx;

    
    public void setupGame() {
        System.out.println("--- 遊戲開始 ---");
        System.out.print("請輸入總玩家人數: ");
        int count = scanner.nextInt();
        players = new Player[count];

        List<Integer> randomIDs = new ArrayList<>();
        for (int i = 0; i < count; i++) randomIDs.add(i);
        Collections.shuffle(randomIDs);

        wolfIdx = randomIDs.get(0);
        witchIdx = randomIDs.get(1);
        hunterIdx = randomIDs.get(2);

        for (int i = 0; i < count; i++) {
            // 顯示名稱為 1~N
            String pName = "玩家" + (i + 1);
            if (i == wolfIdx) players[i] = new Player(pName, "狼人", "Bad");
            else if (i == witchIdx) players[i] = new Player(pName, "女巫", "Good");
            else if (i == hunterIdx) players[i] = new Player(pName, "獵人", "Good");
            else players[i] = new Player(pName, "村民", "Good");
        }
        // 顯示結果時也 +1
        System.out.println("\n狼人:" + (wolfIdx+1) + ", 女巫:" + (witchIdx+1) + ", 獵人:" + (hunterIdx+1) + "\n");
    }

    private boolean isValidTarget(int idx) {
        return idx >= 0 && idx < players.length && players[idx].isAlive();
    }

    // --- 3. 夜晚階段 ---
    public void nightPhase() {
        System.out.println("\n=== 天黑請閉眼 ===");
        
        while (true) {
            System.out.println("[狼人行動] 請輸入要殺害的玩家編號 (1~" + players.length + "):");
            int input = scanner.nextInt();
            killedByWolf = input - 1; 
            if (isValidTarget(killedByWolf)) break;
            System.out.println("錯誤：該玩家已死或不存在，請重新輸入！");
        }
        
        Player witch = players[witchIdx];
        if (witch.isAlive() && witch.canUseSkill()) {
            System.out.println("[女巫行動] 昨晚死的是 " + (killedByWolf + 1) + "，要救嗎？(1:是 / 0:否):");
            if (scanner.nextInt() == 1) {
                killedByWolf = -1;
                witch.useSkill();
            } else {
                System.out.println("要毒人嗎？輸入玩家編號 (-1 跳過):");
                int poisonInput = scanner.nextInt();
                if (poisonInput != -1) {
                    int poisonIdx = poisonInput - 1;
                    if (isValidTarget(poisonIdx)) {
                        players[poisonIdx].setAlive(false);
                        System.out.println("玩家 " + poisonInput + " 被女巫毒死了。");
                        witch.useSkill();
                    }
                }
            }
        }

        if (killedByWolf != -1) {
            players[killedByWolf].setAlive(false);
            System.out.println("昨晚，玩家 " + (killedByWolf + 1) + " 死了。");
            checkHunter(killedByWolf);
        } else System.out.println("昨晚是個平安夜。");
    }

    public void checkHunter(int deadIdx) {
        if (deadIdx == hunterIdx) {
            System.out.println("[獵人技能] 我是獵人(" + (hunterIdx+1) + ")！開槍帶走一人，輸入編號:");
            int shootInput = scanner.nextInt();
            int shootIdx = shootInput - 1;
            if (isValidTarget(shootIdx)) {
                players[shootIdx].setAlive(false);
                System.out.println("獵人開槍帶走了玩家 " + shootInput);
            }
        }
    }

    public void dayPhase() {
        System.out.println("\n=== 白天投票階段 ===");
        System.out.print("存活玩家：");
        for (int i = 0; i < players.length; i++) {
            if (players[i].isAlive()) System.out.print((i + 1) + " ");
        }
        
        int voteIdx;
        while (true) {
            System.out.print("\n請投票選擇放逐的玩家編號: ");
            int voteInput = scanner.nextInt();
            voteIdx = voteInput - 1;
            if (isValidTarget(voteIdx)) break;
            System.out.println("錯誤：請投給活著的人！");
        }
        players[voteIdx].setAlive(false);
        System.out.println("玩家 " + (voteIdx + 1) + " 被投票放逐。");
        checkHunter(voteIdx);
    }

    public boolean checkWinCondition() {
        int wolfCount = 0, goodCount = 0;
        for (Player p : players) {
            if (p.isAlive()) {
                if (p.getSide().equals("Bad")) wolfCount++;
                else goodCount++;
            }
        }
        if (wolfCount == 0) {
            System.out.println("\n>>>> 遊戲結束：好人獲勝！ <<<<");
            return true;
        } else if (wolfCount >= goodCount) {
            System.out.println("\n>>>> 遊戲結束：狼人獲勝！ <<<<");
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        WerewolfGame game = new WerewolfGame();
        game.setupGame();
        while (true) {
            game.nightPhase();
            if (game.checkWinCondition()) break;
            game.dayPhase();
            if (game.checkWinCondition()) break;
        }
    }
}