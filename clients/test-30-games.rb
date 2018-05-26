ids = (1..30)
secrets = (1..30).map { |id| "EdgQWhJ!v&#{id}" }

def run(id, secret)
  Thread.new { `cd python && PLAYER_ID=#{id} PLAYER_SECRET="#{secret}" python play.py -c` }
end

threads = ids.zip(secrets).map { |id, secret| run(id, secret) }

threads.each { |t| t.join }
