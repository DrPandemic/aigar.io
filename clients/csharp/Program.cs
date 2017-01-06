using System;
using System.Threading.Tasks;
using Flurl;
using Flurl.Http;


namespace ConsoleApplication
{
    public class Program
    {
        public static void Main(string[] args)
        {
            Task.Run(async () =>
            {
                var getResp = await "http://www.github.com".GetStringAsync();
                Console.WriteLine(getResp);
            }).Wait();
        }
    }
}