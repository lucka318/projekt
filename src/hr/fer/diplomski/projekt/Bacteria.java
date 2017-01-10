package hr.fer.diplomski.projekt;

public class Bacteria {
	
	 private String name;
	 private String id;
	 
	 private String corpus;
	 
	 public Bacteria(String name, String id, String corpus) {
		this.name = name;
		this.id = id;
		this.corpus = corpus;
	}

	@Override
	public String toString() {
		return "Bacteria [id=" + id + ", corpus=" + corpus + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bacteria other = (Bacteria) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	 

}
