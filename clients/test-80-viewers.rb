require 'net/https'

def run()
  Thread.new {
    while 1 do 
      uri = URI.parse('https://jdis-ia.dinf.usherbrooke.ca/api/1/game/-1')
      req = Net::HTTP::Get.new(uri.path)
      res = Net::HTTP.start(
          uri.host, uri.port, 
          :use_ssl => uri.scheme == 'https', 
          :verify_mode => OpenSSL::SSL::VERIFY_NONE) do |https|
        https.request(req)
      end
      sleep 1/3
    end
  }
end

ids = (1..80)

threads = ids.map { || run() }
threads.each { |t| t.join }
