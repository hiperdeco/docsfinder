package io.github.hiperdeco.docsfinder.main;

public class TesteRegExp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String text = "Isso tem * e + e + e ? também - com* e*.* ";
		
		System.out.println(text.replaceAll("[*,?,+,-]",""));
		
		System.out.println(text.split("[*,?,+,-]").length);
		
		System.out.println(text.replaceAll("([*,?,+,-])","<b>$1</b>"));

	}

}
