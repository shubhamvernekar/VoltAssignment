import java.util.*;

class Appointment {
    private String id;
    private String operatorId;
    private int startTime;
    private int endTime;

    public Appointment(String operatorId, int startTime, int endTime) {
        this.operatorId = operatorId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setStartTime(int i) {
        startTime = i;
    }

    public void setEndTime(int i) {
        endTime = i;
    }
}

class ServiceOperator {
    private String id;
    private List<Appointment> appointments = new ArrayList<>();
    private boolean hasAppointment = false;

    public ServiceOperator(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }


    public boolean hasAppointment() {
        return hasAppointment;
    }

    public void setHasAppointment(boolean hasAppointment) {
        this.hasAppointment = hasAppointment;
    }
}

class AppointmentController {
    public Map<String, ServiceOperator> operators = new HashMap<>();

    public void initializeOperators() {
        operators.put("ServiceOperator0", new ServiceOperator("ServiceOperator0"));
        operators.put("ServiceOperator1", new ServiceOperator("ServiceOperator1"));
        operators.put("ServiceOperator2", new ServiceOperator("ServiceOperator2"));
    }

    public ServiceOperator getOperatorById(String id) {
        return operators.get(id);
    }

    public Appointment findAppointmentByStartTime(ServiceOperator operator, int startTime) {
        for (Appointment appointment : operator.getAppointments()) {
            if (appointment.getStartTime() == startTime) {
                return appointment;
            }
        }
        return null;
    }

    public void bookAppointment(Appointment appointment) {
        ServiceOperator operator = operators.get(appointment.getOperatorId());
        if (operator == null) {
            System.out.println("Operator not found.");
            return;
        }

        if (!isSlotAvailable(operator, appointment.getStartTime(), appointment.getEndTime())) {
            System.out.println("Appointment slot is not available.");
            return;
        }

        if (operator.hasAppointment()) {
            System.out.println("Operator already has an appointment for the day.");
            return;
        }

        operator.setHasAppointment(true);
        operator.getAppointments().add(appointment);
        System.out.println("Appointment booked successfully.");
    }

    public void rescheduleAppointment(Appointment oldAppointment, int newStartTime, int newEndTime) {
        ServiceOperator operator = operators.get(oldAppointment.getOperatorId());
        if (operator == null) {
            System.out.println("Operator not found.");
            return;
        }

        if (!isSlotAvailable(operator, newStartTime, newEndTime)) {
            System.out.println("Appointment slot is not available.");
            return;
        }

        oldAppointment.setStartTime(newStartTime);
        oldAppointment.setEndTime(newEndTime);
        System.out.println("Appointment rescheduled successfully.");
    }

    public void cancelAppointment(Appointment appointment) {
        ServiceOperator operator = operators.get(appointment.getOperatorId());
        if (operator == null) {
            System.out.println("Operator not found.");
            return;
        }

        operator.getAppointments().remove(appointment);
        System.out.println("Appointment canceled successfully.");
    }

    private boolean isSlotAvailable(ServiceOperator operator, int startTime, int endTime) {
        for (Appointment existingAppointment : operator.getAppointments()) {
            if (existingAppointment.getStartTime() < endTime &&
                existingAppointment.getEndTime() > startTime) {
                return false;
            }
        }
        return true;
    }

    public void showBookedAppointments(ServiceOperator operator) {
        System.out.println("Booked appointments for " + operator.getId() + ":");
        for (Appointment appointment : operator.getAppointments()) {
            System.out.println(appointment.getStartTime() + "-" + appointment.getEndTime());
        }
    }

    public void showOpenSlots(ServiceOperator operator) {
        List<String> openSlots = new ArrayList<>();
        int currentSlotStart = 0;
        boolean isSlotOpen = true;

        for (int hour = 0; hour < 24; hour++) {
            isSlotOpen = true;

            for (Appointment appointment : operator.getAppointments()) {
                if (hour >= appointment.getStartTime() && hour < appointment.getEndTime()) {
                    isSlotOpen = false;
                    break;
                }
            }

            if (isSlotOpen) {
                if (currentSlotStart == -1) {
                    currentSlotStart = hour;
                }
            } else {
                if (currentSlotStart != -1) {
                    int currentSlotEnd = hour;
                    openSlots.add(currentSlotStart + "-" + currentSlotEnd);
                    currentSlotStart = -1;
                }
            }
        }

        if (currentSlotStart != -1) {
            openSlots.add(currentSlotStart + "-23");
        }

        System.out.println("Open slots for " + operator.getId() + ":");
        for (String slot : openSlots) {
            System.out.println(slot);
        }
    }
}

public class SchedulerApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AppointmentController controller = new AppointmentController();

        controller.initializeOperators();

        while (true) {
            System.out.println("Select an option:");
            System.out.println("1. Book Appointment");
            System.out.println("2. Reschedule Appointment");
            System.out.println("3. Cancel Appointment");
            System.out.println("4. Show Booked Appointments");
            System.out.println("5. Show Open Slots");
            System.out.println("6. Exit");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    scanner.nextLine();
                    System.out.print("Enter Operator ID: ");
                    String operatorId = scanner.nextLine();
                    System.out.print("Enter Start Time (0-23): ");
                    int startTime = scanner.nextInt();
                    System.out.print("Enter End Time (1-24): ");
                    int endTime = scanner.nextInt();
                    Appointment appointment = new Appointment(operatorId, startTime, endTime);
                    controller.bookAppointment(appointment);
                    break;
                case 2:
                    scanner.nextLine();
                    System.out.print("Enter Operator ID: ");
                    String rescheduleOperatorId = scanner.nextLine();
                    ServiceOperator rescheduleOperator = controller.getOperatorById(rescheduleOperatorId);
                    if (rescheduleOperator != null) {
                        System.out.print("Enter Start Time of the appointment to reschedule (0-23): ");
                        int rescheduleStartTime = scanner.nextInt();
                        Appointment appointmentToReschedule = controller.findAppointmentByStartTime(rescheduleOperator, rescheduleStartTime);

                        if (appointmentToReschedule != null) {
                            System.out.print("Enter New Start Time (0-23): ");
                            int newStartTime = scanner.nextInt();
                            System.out.print("Enter New End Time (1-24): ");
                            int newEndTime = scanner.nextInt();

                            controller.rescheduleAppointment(appointmentToReschedule, newStartTime, newEndTime);
                        } else {
                            System.out.println("Appointment not found for the given start time.");
                        }
                    } else {
                        System.out.println("Operator not found.");
                    }
                    break;
                case 3:
                    scanner.nextLine();
                    System.out.print("Enter Operator ID: ");
                    String cancelOperatorId = scanner.nextLine();
                    ServiceOperator cancelOperator = controller.getOperatorById(cancelOperatorId);
                    if (cancelOperator != null) {
                        System.out.print("Enter Start Time of the appointment to cancel (0-23): ");
                        int cancelStartTime = scanner.nextInt();
                        Appointment appointmentToCancel = controller.findAppointmentByStartTime(cancelOperator, cancelStartTime);

                        if (appointmentToCancel != null) {
                            controller.cancelAppointment(appointmentToCancel);
                        } else {
                            System.out.println("Appointment not found for the given start time.");
                        }
                    } else {
                        System.out.println("Operator not found.");
                    }
                    break;
                case 4:
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter Operator ID: ");
                    String bookedOperatorId = scanner.nextLine();
                    ServiceOperator bookedOperator = controller.getOperatorById(bookedOperatorId);
                    if (bookedOperator != null) {
                        controller.showBookedAppointments(bookedOperator);
                    } else {
                        System.out.println("Operator not found.");
                    }
                    break;
                case 5:
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter Operator ID: ");
                    String openSlotsOperatorId = scanner.nextLine();
                    ServiceOperator openSlotsOperator = controller.getOperatorById(openSlotsOperatorId);
                    if (openSlotsOperator != null) {
                        controller.showOpenSlots(openSlotsOperator);
                    } else {
                        System.out.println("Operator not found.");
                    }
                    break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
