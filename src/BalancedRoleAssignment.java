import java.util.*;

public class BalancedRoleAssignment {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int numNames;
        do {
            System.out.print("Enter the number of names (4 or 8): ");
            numNames = scanner.nextInt();
        } while (numNames != 4 && numNames != 8);

        scanner.nextLine(); // Consume the newline character

        String[] names = new String[numNames];
        Map<String, Set<String>> playerRoles = new HashMap<>();
        Set<String> allRoles = new HashSet<>(Arrays.asList("healer", "dps", "tank", "h", "d", "t"));

        for (int i = 0; i < numNames; i++) {
            System.out.print("Enter name " + (i + 1) + ": ");
            names[i] = scanner.nextLine();

            System.out.print("Assign roles for " + names[i] + " (healer/dps/tank, separated by commas): ");
            String roleInput = scanner.nextLine().toLowerCase();

            String[] roleTokens = roleInput.split(",");
            Set<String> playerRoleSet = new HashSet<>();

            for (String roleToken : roleTokens) {
                String trimmedRole = roleToken.trim();
                if (allRoles.contains(trimmedRole)) {
                    switch (trimmedRole) {
                        case "h" -> playerRoleSet.add("healer");
                        case "d" -> playerRoleSet.add("dps");
                        case "t" -> playerRoleSet.add("tank");
                        default -> playerRoleSet.add(trimmedRole);
                    }
                } else {
                    System.out.println("Invalid role assignment: " + trimmedRole);
                }
            }

            if (playerRoleSet.isEmpty()) {
                System.out.println(names[i] + " must have at least one role. Role set to 'dps'.");
                playerRoleSet.add("dps");
            }

            playerRoles.put(names[i], playerRoleSet);
        }

        boolean rolesBalanced = generateAssignments(playerRoles, numNames);

        if (rolesBalanced) {
            System.out.println("Roles are balanced. Final role assignments:");
            for (Map.Entry<String, Set<String>> entry : playerRoles.entrySet()) {
                String roles = String.join(", ", entry.getValue());
                System.out.println(entry.getKey() + ": " + roles);
            }
        } else {
            System.out.print("The 1:2:1 ratio cannot be met. The following roles need more people to be able to work:");
            List<String> rolesNeeded = getRolesNeeded(playerRoles, numNames);
            System.out.println(String.join(", ", rolesNeeded));
        }

        System.out.println("Players: " + String.join(", ", names));

        boolean editMore = true;
        while (editMore) {
            System.out.println("Current player roles:");
            for (Map.Entry<String, Set<String>> entry : playerRoles.entrySet()) {
                String roles = String.join(", ", entry.getValue());
                System.out.println(entry.getKey() + ": " + roles);
            }

            System.out.print("Enter the name of the player you want to edit (or 'done' to finish editing): ");
            String playerToEdit = scanner.nextLine();

            if (playerToEdit.equalsIgnoreCase("done")) {
                editMore = false;
            } else if (playerRoles.containsKey(playerToEdit)) {
                editPlayerRoles(playerRoles, playerToEdit);

                rolesBalanced = generateAssignments(playerRoles, numNames);
                if (rolesBalanced) {
                    System.out.println("Roles are balanced after editing. Final role assignments:");
                    printFinalAssignments(playerRoles);
                    editMore = false;
                }
            } else {
                System.out.println("Player not found.");
            }
        }

        scanner.close();
    }

    private static boolean generateAssignments(Map<String, Set<String>> playerRoles, int numNames) {
        List<String> healers = new ArrayList<>();
        List<String> dpsPlayers = new ArrayList<>();
        List<String> tanks = new ArrayList<>();

        for (Set<String> roles : playerRoles.values()) {
            if (roles.contains("healer")) {
                healers.add("healer");
            }
            if (roles.contains("dps")) {
                dpsPlayers.add("dps");
            }
            if (roles.contains("tank")) {
                tanks.add("tank");
            }
        }

        int totalRoles = healers.size() + dpsPlayers.size() + tanks.size();
        int targetHealers = numNames / 4;
        int targetDPS = numNames / 2;
        int targetTanks = numNames / 4;

        return healers.size() >= targetHealers && dpsPlayers.size() >= targetDPS && tanks.size() >= targetTanks;
    }

    private static void editPlayerRoles(Map<String, Set<String>> playerRoles, String playerName) {
        Set<String> allRoles = new HashSet<>(Arrays.asList("healer", "dps", "tank", "h", "d", "t"));

        if (playerRoles.containsKey(playerName)) {
            Set<String> currentRoles = playerRoles.get(playerName);
            System.out.println("Current roles for " + playerName + ": " + String.join(", ", currentRoles));

            System.out.print("Edit roles for " + playerName + " (healer/dps/tank, separated by commas): ");
            Scanner scanner = new Scanner(System.in);
            String roleInput = scanner.nextLine().toLowerCase();

            String[] roleTokens = roleInput.split(",");
            Set<String> playerRoleSet = new HashSet<>();

            for (String roleToken : roleTokens) {
                String trimmedRole = roleToken.trim();
                if (allRoles.contains(trimmedRole)) {
                    switch (trimmedRole) {
                        case "h" -> playerRoleSet.add("healer");
                        case "d" -> playerRoleSet.add("dps");
                        case "t" -> playerRoleSet.add("tank");
                        default -> playerRoleSet.add(trimmedRole);
                    }
                } else {
                    System.out.println("Invalid role assignment: " + trimmedRole);
                }
            }

            if (playerRoleSet.isEmpty()) {
                System.out.println(playerName + " must have at least one role. Role set to 'dps'.");
                playerRoleSet.add("dps");
            }

            playerRoles.put(playerName, playerRoleSet);

            System.out.println(playerName + " roles updated: " + String.join(", ", playerRoleSet));
        } else {
            System.out.println(playerName + " not found.");
        }
    }

    private static List<String> getRolesNeeded(Map<String, Set<String>> playerRoles, int numNames) {
        List<String> rolesNeeded = new ArrayList<>();
        int numHealers = 0;
        int numDPS = 0;
        int numTanks = 0;

        for (Set<String> roles : playerRoles.values()) {
            if (roles.contains("healer")) {
                numHealers++;
            }
            if (roles.contains("dps")) {
                numDPS++;
            }
            if (roles.contains("tank")) {
                numTanks++;
            }
        }

        int targetHealers = numNames / 4;
        int targetDPS = numNames / 2;
        int targetTanks = numNames / 4;

        if (numHealers < targetHealers) {
            rolesNeeded.add("healer");
        }
        if (numDPS < targetDPS) {
            rolesNeeded.add("dps");
        }
        if (numTanks < targetTanks) {
            rolesNeeded.add("tank");
        }

        return rolesNeeded;
    }

    private static void printFinalAssignments(Map<String, Set<String>> playerRoles) {
        List<String> tanks = new ArrayList<>();
        List<String> dpsPlayers = new ArrayList<>();
        List<String> healers = new ArrayList<>();

        for (Map.Entry<String, Set<String>> entry : playerRoles.entrySet()) {
            if (entry.getValue().contains("tank")) {
                tanks.add(entry.getKey());
            } else if (entry.getValue().contains("dps")) {
                dpsPlayers.add(entry.getKey());
            } else if (entry.getValue().contains("healer")) {
                healers.add(entry.getKey());
            }
        }

        System.out.println("Final role assignments:");
        System.out.println("Tanks: " + String.join(", ", tanks));
        System.out.println("DPS: " + String.join(", ", dpsPlayers));
        System.out.println("Healers: " + String.join(", ", healers));
    }
}
