package audinss.utils;

public abstract class UtilsMethods {
	
	public static String mesRef(int mes, int ano) {
		String mesRef = "";

		switch (mes) {
		case 1:
              mesRef = "jan/"+ano;
			break;
		case 2:
			  mesRef = "fev/"+ano;
			break;
		case 3:
			  mesRef = "mar/"+ano;  
			break;
		case 4:
			 mesRef = "abr/"+ano; 
			break;
		case 5:
			 mesRef = "mai/"+ano;
			break;
		case 6:
			mesRef = "jun/"+ano;
			break;
		case 7:
			mesRef = "jul/"+ano;
			break;
		case 8:
			mesRef = "ago/"+ano;
			break;
		case 9:
			mesRef = "set/"+ano;  
			break;
		case 10:
			mesRef = "out/"+ano;  
			break;
		case 11:
			mesRef = "nov/"+ano;
			break;
		case 12:
			mesRef = "dez/"+ano;
			break;
		case 13:
			mesRef = "Terc. Sal. "+ano;
			break;	
			
		default:
			mesRef = "";
			break;
		}
		
		return mesRef;
		
	}

}
