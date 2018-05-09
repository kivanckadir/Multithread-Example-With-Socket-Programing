package job;

public class Job {
	
	private int id;
	private double length;
	private String type;
	private boolean isAccepted = false;
	
	public Job(int id, double jobLength, String type) {
		super();
		this.id = id;
		this.length = jobLength;
		this.type = type;
	}

	public Job() {
		super();
	}

	public int getId() {
		return id;
	}

	public double getLength() {
		return length;
	}

	public String getType() {
		return type;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLength(double jobLength) {
		this.length = jobLength;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

	@Override
	public String toString() {
		return "Job [id=" + id + ", length=" + length + ", type=" + type + ", isAccepted=" + isAccepted + "]";
	}
}
