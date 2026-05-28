import { Trophy, Clock, Users, Award, Upload } from 'lucide-react';

interface CompetitionsProps {
  user: any;
}

export function Competitions({ user }: CompetitionsProps) {
  const competitions = [
    {
      id: 1,
      name: 'Photo Challenge 2024',
      type: 'Photography',
      prize: '$2,000',
      entryFee: 10,
      endTime: '5d 12h',
      participants: 234,
      description: 'Capture the beauty of winter',
      image: 'https://images.unsplash.com/photo-1452421822248-d4c2b47f0c81?w=400',
      status: 'active',
    },
    {
      id: 2,
      name: 'Creative Writing Contest',
      type: 'Writing',
      prize: '$1,500',
      entryFee: 5,
      endTime: '3d 8h',
      participants: 456,
      description: 'Write a story about adventure',
      image: 'https://images.unsplash.com/photo-1455390582262-044cdead277a?w=400',
      status: 'active',
    },
    {
      id: 3,
      name: 'Design Challenge',
      type: 'Design',
      prize: '$3,000',
      entryFee: 15,
      endTime: '7d 4h',
      participants: 189,
      description: 'Create a modern logo design',
      image: 'https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400',
      status: 'active',
    },
    {
      id: 4,
      name: 'Cooking Competition',
      type: 'Culinary',
      prize: '$2,500',
      entryFee: 20,
      endTime: '2d 18h',
      participants: 312,
      description: 'Best holiday recipe',
      image: 'https://images.unsplash.com/photo-1556910103-1c02745aae4d?w=400',
      status: 'active',
    },
    {
      id: 5,
      name: 'Video Creator Awards',
      type: 'Video',
      prize: '$5,000',
      entryFee: 25,
      endTime: '10d 6h',
      participants: 567,
      description: 'Best short film under 5 minutes',
      image: 'https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=400',
      status: 'active',
    },
    {
      id: 6,
      name: 'Music Production Battle',
      type: 'Music',
      prize: '$4,000',
      entryFee: 30,
      endTime: '8d 14h',
      participants: 198,
      description: 'Create an original track',
      image: 'https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=400',
      status: 'active',
    },
  ];

  const myEntries = [
    {
      id: 1,
      competition: 'Photo Challenge 2024',
      submittedDate: '2 days ago',
      status: 'Under Review',
      votes: 45,
    },
    {
      id: 2,
      competition: 'Creative Writing Contest',
      submittedDate: '5 days ago',
      status: 'Published',
      votes: 128,
    },
  ];

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl mb-2">Competitions</h1>
          <p className="text-gray-600">
            Showcase your talents and compete for amazing prizes
          </p>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-600">Your Balance</div>
          <div className="text-2xl text-purple-600">${user?.balance.toFixed(2)}</div>
        </div>
      </div>

      {/* My Entries */}
      <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <h2 className="text-xl mb-4">My Competition Entries</h2>
        <div className="space-y-3">
          {myEntries.map((entry) => (
            <div
              key={entry.id}
              className="flex items-center justify-between p-4 bg-gradient-to-r from-purple-50 to-pink-50 rounded-xl"
            >
              <div className="flex items-center space-x-4">
                <div className="p-2 bg-white rounded-lg">
                  <Trophy className="w-5 h-5 text-purple-600" />
                </div>
                <div>
                  <div className="mb-1">{entry.competition}</div>
                  <div className="text-sm text-gray-600">
                    Submitted {entry.submittedDate}
                  </div>
                </div>
              </div>
              <div className="text-right">
                <div className="inline-flex items-center px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm mb-1">
                  {entry.status}
                </div>
                <div className="text-sm text-gray-600">{entry.votes} votes</div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Active Competitions */}
      <div>
        <h2 className="text-xl mb-4">Active Competitions</h2>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {competitions.map((competition) => (
            <div
              key={competition.id}
              className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden hover:shadow-md transition-shadow"
            >
              <div className="relative h-48">
                <img
                  src={competition.image}
                  alt={competition.name}
                  className="w-full h-full object-cover"
                />
                <div className="absolute top-4 left-4">
                  <div className="px-3 py-1 bg-white/90 backdrop-blur-sm rounded-full text-sm">
                    {competition.type}
                  </div>
                </div>
              </div>

              <div className="p-6">
                <h3 className="text-xl mb-2">{competition.name}</h3>
                <p className="text-sm text-gray-600 mb-4">{competition.description}</p>

                <div className="space-y-2 mb-4 text-sm">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center text-gray-600">
                      <Award className="w-4 h-4 mr-2" />
                      Prize
                    </div>
                    <div className="text-purple-600">{competition.prize}</div>
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center text-gray-600">
                      <Clock className="w-4 h-4 mr-2" />
                      Ends in
                    </div>
                    <div>{competition.endTime}</div>
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center text-gray-600">
                      <Users className="w-4 h-4 mr-2" />
                      Entries
                    </div>
                    <div>{competition.participants}</div>
                  </div>
                </div>

                <div className="pt-4 border-t border-gray-100">
                  <div className="flex items-center justify-between mb-3">
                    <div className="text-gray-600">Entry Fee</div>
                    <div className="text-xl">${competition.entryFee}</div>
                  </div>
                  <button className="w-full py-3 bg-gradient-to-r from-purple-600 to-pink-600 text-white rounded-xl hover:shadow-lg transition-shadow flex items-center justify-center space-x-2">
                    <Upload className="w-5 h-5" />
                    <span>Submit Entry</span>
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Competition Rules */}
      <div className="bg-gradient-to-r from-purple-600 to-pink-600 p-8 rounded-2xl text-white">
        <h2 className="text-2xl mb-4">How Competitions Work</h2>
        <div className="grid md:grid-cols-3 gap-6">
          <div>
            <div className="text-3xl mb-2">1</div>
            <div className="text-lg mb-2">Choose & Pay</div>
            <p className="text-purple-100">
              Select a competition and pay the entry fee to participate
            </p>
          </div>
          <div>
            <div className="text-3xl mb-2">2</div>
            <div className="text-lg mb-2">Submit Entry</div>
            <p className="text-purple-100">
              Upload your work following the competition guidelines
            </p>
          </div>
          <div>
            <div className="text-3xl mb-2">3</div>
            <div className="text-lg mb-2">Win Prizes</div>
            <p className="text-purple-100">
              Get voted by the community and win amazing prizes
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
