
import java.sql.*;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== Hostel Management =====");
            System.out.println("1. Add Room");
            System.out.println("2. Allot Room to Student");
            System.out.println("3. Vacate Student");
            System.out.println("4. View Room Status");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1: addRoom(); break;
                case 2: allotRoom(); break;
                case 3: vacateStudent(); break;
                case 4: viewRoomStatus(); break;
                case 5: System.exit(0);
                default: System.out.println("Invalid choice!");
            }
        }
    }

    static void addRoom() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter Room No: ");
            String roomNo = sc.next();

            System.out.print("Enter Floor: ");
            int floor = sc.nextInt();

            System.out.print("Enter Capacity: ");
            int capacity = sc.nextInt();

            String query = "INSERT INTO rooms(room_no, floor, capacity) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, roomNo);
            ps.setInt(2, floor);
            ps.setInt(3, capacity);

            ps.executeUpdate();
            System.out.println("Room added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void allotRoom() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter Room No: ");
            String roomNo = sc.next();

            System.out.print("Enter Student Name: ");
            sc.nextLine();
            String student = sc.nextLine();

            String q1 = "SELECT room_id, capacity FROM rooms WHERE room_no=?";
            PreparedStatement ps1 = con.prepareStatement(q1);
            ps1.setString(1, roomNo);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                System.out.println("Room not found!");
                return;
            }

            int roomId = rs.getInt("room_id");
            int capacity = rs.getInt("capacity");

            String q2 = "SELECT COUNT(*) FROM allotments WHERE room_id=? AND vacated=FALSE";
            PreparedStatement ps2 = con.prepareStatement(q2);
            ps2.setInt(1, roomId);
            ResultSet rs2 = ps2.executeQuery();
            rs2.next();

            int occupied = rs2.getInt(1);

            if (occupied >= capacity) {
                System.out.println("Room is FULL!");
                return;
            }

            String q3 = "INSERT INTO allotments(room_id, student) VALUES (?, ?)";
            PreparedStatement ps3 = con.prepareStatement(q3);
            ps3.setInt(1, roomId);
            ps3.setString(2, student);

            ps3.executeUpdate();
            System.out.println("Room allotted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void vacateStudent() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter Room No: ");
            String roomNo = sc.next();

            System.out.print("Enter Student Name: ");
            sc.nextLine();
            String student = sc.nextLine();

            String q1 = "SELECT room_id FROM rooms WHERE room_no=?";
            PreparedStatement ps1 = con.prepareStatement(q1);
            ps1.setString(1, roomNo);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                System.out.println("Room not found!");
                return;
            }

            int roomId = rs.getInt("room_id");

            String q2 = "UPDATE allotments SET vacated=TRUE WHERE student=? AND room_id=? AND vacated=FALSE";
            PreparedStatement ps2 = con.prepareStatement(q2);
            ps2.setString(1, student);
            ps2.setInt(2, roomId);

            int rows = ps2.executeUpdate();

            if (rows > 0) {
                System.out.println("Student vacated successfully!");
            } else {
                System.out.println("No active allotment found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void viewRoomStatus() {
        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT r.room_no, r.floor, r.capacity, COUNT(a.allot_id) AS occupied " +
                    "FROM rooms r LEFT JOIN allotments a " +
                    "ON r.room_id = a.room_id AND a.vacated=FALSE GROUP BY r.room_id";

            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int available = rs.getInt("capacity") - rs.getInt("occupied");

                System.out.println("Room " + rs.getString("room_no") +
                        " | Floor " + rs.getInt("floor") +
                        " | Capacity " + rs.getInt("capacity") +
                        " | Occupied " + rs.getInt("occupied") +
                        " | Available " + available +
                        (available == 0 ? " [FULL]" : ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
