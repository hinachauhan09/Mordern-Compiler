import java.util.*;
import java.io.*;

class ShiftReduction
{
	Table table;
	String inputString="id*id+id$";
	ArrayList<String> parse=new ArrayList<>();
	ArrayList<ArrayList<String>> output=new ArrayList<>();
	String[][] p={
					{"E","E+T"},
					{"E","T"},
					{"T","T*F"},
					{"T","F"},
					{"F","(E)"},
					{"F","id"}
				  };
	Production prod;
	
	//vars for lr1
	int starti=0;
	String next="",oldpop="";		
	String stack=""+starti,newStack="",ip="",action="",op="";
		
	ShiftReduction()
	{
		fileHandler();
		prod=new Production(p);
		
		System.out.println("\nAction Table & Go To Table :");
		System.out.println(table.displayTitle());
		for(int i=0;i<table.size();i++)
			System.out.println(table.displayRow(i));
		
		parse=parseInput(inputString);
		System.out.println("\nIdentifiers :");
		for(int i=0;i<parse.size();i++)
			System.out.print(parse.get(i)+"\t");
		
		System.out.println("\n\nProductions :");
		System.out.println(prod.displayAll());
		
		lr1();
		
		System.out.println("\nOutput :");
		//System.out.println("Stack\t\tInput\t\tAction\t\tOutput");
		System.out.printf("%-30s%-30s%-30s%-30s%n","Stack","Input","Action","Output");
		for(int i=0;i<output.size();i++)
		{
			ArrayList<String> temp=output.get(i);
			for(int j=0;j<temp.size();j++)
				System.out.printf("%-30s",temp.get(j));
			
			// for(int j=0;j<temp.size();j++)
				// System.out.println(temp.get(j)+"\t\t");
			
			System.out.println();
		}	
	}
	
	ArrayList<String> parseInput(String input)
	{
		String d="";
		ArrayList<String> data=new ArrayList<>();
		for(int i=0;i<input.length();i++)
		{
			if(Character.isLowerCase(input.charAt(i)))
			{	
				d=d+input.charAt(i);
			}
			if(!Character.isLetter(input.charAt(i)))
			{
				data.add(d);
				d="";
				data.add(Character.toString(input.charAt(i)));
			}			
		}
		return data;
	}
	
	void lr1()
	{

		while(true)
		{
			ip="";
			ArrayList<String> out=new ArrayList<>();
			String pop=parse.get(0);
			
			op="";
			next=table.getSpecific(pop,starti);
			
			for(int i=0;i<parse.size();i++)
					ip=ip+parse.get(i);
			
			if(next.startsWith("s"))
			{
				starti=iterateNewI(next);
				newStack=shiftFunction(stack,pop,starti);
				oldpop=parse.remove(0);
				action="Shift "+starti;
				//System.out.println(stack+"\t\t"+ip+"\t\t"+action+"\t\t");
			}
			else if(next.startsWith("r"))
			{	
				newStack=reduceFunction(stack,Integer.parseInt(next.substring(1)));
				action="Reduce by "+prod.display(Integer.parseInt(next.substring(1))-1);
				op=prod.display(Integer.parseInt(next.substring(1))-1);
				starti=iterateNewI(newStack);
				//System.out.println(stack+"\t\t"+ip+"\t\t"+action+"\t\t"+op);
			}
			else if(next.equals("-"))
			{
				System.out.println("\nError State.. Not a valid String");
				System.out.println(stack+"\t\t"+ip+"\t\t"+action+"\t"+op);
				break;
			}
			
			out.add(stack);
			out.add(ip);
			out.add(action);
			out.add(op);
			//System.out.println(out.get(0)+"\t\t"+out.get(1)+"\t\t"+out.get(2)+"\t\t"+out.get(3));
			output.add(out);
			
			if(next.equals("Accept"))
			{
				//System.out.println(stack+"\t\t"+ip+"\t\t"+action+"\t"+op);
				System.out.println("Accept the String : "+inputString);
				break;
			}
			stack=newStack;
		}
	}
	
	int iterateNewI(String input)
	{
		String temp="";
		for(int i=input.length()-1;i>=0;i--)
		{
			if(Character.isDigit(input.charAt(i)))
				temp=input.charAt(i)+temp;
			else
				break;
		}
		return Integer.parseInt(temp);
	}
	
	String reduceFunction(String stack,int i)
	{
		int index=stack.indexOf(prod.getChild(i-1).substring(0,1));
		
		stack=stack.substring(0,index);
		int state=Integer.parseInt(stack.substring(stack.length()-1));
		stack=stack+prod.getParent(i-1)+table.getSpecific(prod.getParent(i-1),state);
		
		return stack;
	}
	
	String shiftFunction(String stack,String pop,int i)
	{
		return stack+pop+i;
	}
	
	void fileHandler()
	{	
		try{
			BufferedReader br = new BufferedReader(new FileReader("input.csv"));
			String line = "";
			String d[][]=new String[13][9];
			int i=0;
			while ((line = br.readLine()) != null) {             
				d[i] = line.split(",");
				i++;
			}
			table=new Table(d);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	class Production
	{
		ArrayList<ArrayList<String>> prod=new ArrayList<>();
		Production(String p[][])
		{
			for(int i=0;i<p.length;i++)
			{
				ArrayList<String> row=new ArrayList<>();
				for(int j=0;j<p[i].length;j++)
				{
					row.add(p[i][j]);
				}
				prod.add(row);
			}
		}
		String getParent(int i)
		{
			return prod.get(i).get(0);
		}
		String getChild(int i)
		{
			return prod.get(i).get(1);
		}
		String display(int i)
		{
			ArrayList<String> row=prod.get(i);
			return row.get(0)+" -> "+row.get(1);
		}
		String displayAll()
		{
			String data="";
			for(int i=0;i<prod.size();i++)
			{
				ArrayList<String> row=prod.get(i);
				data=data+row.get(0)+" -> "+row.get(1)+"\n";
			}
			return data;
		}
	}
	
	class Table
	{ 
		ArrayList<ArrayList<String>> table=new ArrayList<>();
		ArrayList<String> title=new ArrayList<>();
		Table(String[][] data)
		{
			for(int i=0;i<data[0].length;i++)
			{
				title.add(data[0][i]);
			}
			for(int i=1;i<data.length;i++)
			{
				ArrayList<String> row=new ArrayList<>();
				for(int j=0;j<data[i].length;j++)
				{
					row.add(data[i][j]);
				}
				table.add(row);
			}
		}
		String[] getRow(int i)
		{
			return table.get(i).toArray(new String[0]);
		}
		String getSpecific(String c,int i)
		{
			int j=title.indexOf(c);
			return table.get(i).get(j);
		}
		String displayRow(int i)
		{
			String data="";
			for(int j=0;j<table.get(i).size();j++)
				data=data+table.get(i).get(j)+"\t";
			return data;
		}
		int size()
		{
			return table.size();
		}
		String displayTitle()
		{
			String data="";
			for(int i=0;i<title.size();i++)
				data=data+title.get(i)+"\t";
			return data;
		}
	}
	
	public static void main(String s[])
	{
		new ShiftReduction();
	}
}