package io.github.hiperdeco.docsfinder.main;

public class TesteRegExp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String text = "Isso tem * E + e + e ? também - com* E*.* ";
		
		System.out.println(text.replaceAll("[*,?,+,-]",""));
		
		System.out.println(text.split("[*,?,+,-]").length);
		
		System.out.println(text.replaceAll("([*,?,+,-])","<b>$1</b>"));
		
		System.out.println(text.replaceAll("(?i)(e)","<b>$1</b>"));
		
		String outro = "Bla *? bla eu tenho .*";
		System.out.println(outro.replaceAll("^[*|?]+", ""));
		String outro1 = "REPO_un_un";
		System.out.println(outro1.replaceAll("_un$", ""));

	}

}
