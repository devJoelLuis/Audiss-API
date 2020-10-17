package audinss.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SenhaEncript {

	public static void main(String[] args) {
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		System.out.println(enc.encode("cpd,9628"));

	}

}
