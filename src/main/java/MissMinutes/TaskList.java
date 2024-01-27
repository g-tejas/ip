package MissMinutes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TaskList implements Serializable {
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public int size() {
        return tasks.size();
    }

    public Task createTask(String input) throws MissMinutesException {
        String[] split = input.split(" ", 2);
        String desc = split.length > 1 ? split[1] : "";

        try {
            if (split[0].equalsIgnoreCase("TODO")) {
                return Todo.fromStr(desc);
            } else if (split[0].equalsIgnoreCase("DEADLINE")) {
                return Deadline.fromStr(desc);
            } else if (split[0].equalsIgnoreCase("EVENT")) {
                return Event.fromStr(desc);
            } else {
                throw new MissMinutesException("Invalid command name, please try again!");
            }
        } catch (ArrayIndexOutOfBoundsException err) {
            throw new MissMinutesException("Incomplete command, please try again!");
        }
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        String reply = "Got it. I've added this task: \n" + task + "\n" +
                "Now you have " + tasks.size() + " tasks in the list.";
        Ui.sendMsg(reply);
    }

    public void deleteTask(String input) throws MissMinutesException {
        String[] split = input.split(" ");
        int idx;
        try {
            idx = Integer.parseInt(split[1]) - 1; // 0 indexed
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException err) {
            throw new MissMinutesException("Please enter a valid index. For e.g, a correct usage is: delete 2");
        }
        try {
            Task curr = this.tasks.get(idx);
            this.tasks.remove(idx);
            String reply = "Noted. I've removed this task:\n"
                    + curr + "\n"
                    + "Now you have " + this.tasks.size() + " tasks in the list.";
            Ui.sendMsg(reply);
        } catch (IndexOutOfBoundsException err) {
            throw new MissMinutesException("This task doesn't exist!", err);
        }
    }

    public void markTask(int idx) throws MissMinutesException {
        try {
            Task curr = this.tasks.get(idx);
            curr.markAsDone();
            String reply = "Nice! I've marked this task as done: \n" + curr;
            Ui.sendMsg(reply);
        } catch (IndexOutOfBoundsException err) {
            throw new MissMinutesException("This task doesn't exist!", err);
        }
    }

    public void unmarkTask(int idx) throws MissMinutesException {
        try {
            Task curr = this.tasks.get(idx);
            curr.unmark();
            String reply = "OK, I've marked this task as not done yet: \n" + curr;
            Ui.sendMsg(reply);
        } catch (IndexOutOfBoundsException err) {
            throw new MissMinutesException("This task doesn't exist!", err);
        }
    }

    public void findTask(String input) throws MissMinutesException {
        String[] split = input.split(" ", 2);
        if (split.length <= 1) {
            throw new MissMinutesException("Invalid find command format. Please enter a keyword, like `find book`");
        }
        String keyword = split[1];

        ArrayList<Task> results = this.tasks
                .stream()
                .filter(task -> task.description.toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (results.isEmpty()) {
            throw new MissMinutesException("No tasks found =(");
        }

        StringBuilder reply = new StringBuilder("Here are the matching tasks in your list: ");
        for (int i = 0; i < results.size(); i++) {
            reply.append("\n")
                    .append((i + 1))
                    .append(". ")
                    .append(results.get(i));
        }
        Ui.sendMsg(reply.toString());
    }
}
