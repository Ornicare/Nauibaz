package fr.ornicare.util;

public class Allele {

	private Gene a;
	private Gene b;
	
	public Allele(Gene a, Gene b) {
		this.a = a;
		this.b = b;
	}
	
	public Gene getDominantGene() {
		if(a.getExpressionValue()==b.getExpressionValue()) {
			if(Math.random()<0.5) return a;
			return b;
		}
		if(a.getExpressionValue()<b.getExpressionValue()) return b;
		return a;
	}
	
	public Gene getRandomGene() {
		if(Math.random()<0.5) return a;
		return b;
	}

	public Gene getA() {
		return a;
	}

	public void setA(Gene a) {
		this.a = a;
	}

	public Gene getB() {
		return b;
	}

	public void setB(Gene b) {
		this.b = b;
	}
	
	
}
