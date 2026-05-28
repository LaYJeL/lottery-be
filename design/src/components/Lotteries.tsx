import { useState } from 'react';
import { Ticket, Clock, Users, TrendingUp, ShoppingCart } from 'lucide-react';

interface LotteriesProps {
  user: any;
}

export function Lotteries({ user }: LotteriesProps) {
  const [selectedLottery, setSelectedLottery] = useState<number | null>(null);
  const [ticketCount, setTicketCount] = useState(1);

  const lotteries = [
    {
      id: 1,
      name: 'Daily Lucky Draw',
      type: 'Daily',
      prize: '$5,000',
      ticketPrice: 5,
      drawTime: '2h 15m',
      participants: 1234,
      odds: '1 in 1,234',
      description: 'Quick daily draw with great odds',
      color: 'from-blue-500 to-cyan-500',
    },
    {
      id: 2,
      name: 'Weekly Mega Jackpot',
      type: 'Weekly',
      prize: '$50,000',
      ticketPrice: 10,
      drawTime: '3d 8h',
      participants: 5678,
      odds: '1 in 5,678',
      description: 'Big weekly jackpot with amazing prizes',
      color: 'from-indigo-500 to-purple-500',
    },
    {
      id: 3,
      name: 'Monthly Grand Prize',
      type: 'Monthly',
      prize: '$500,000',
      ticketPrice: 25,
      drawTime: '12d 5h',
      participants: 12450,
      odds: '1 in 12,450',
      description: 'Life-changing monthly grand prize',
      color: 'from-purple-500 to-pink-500',
    },
    {
      id: 4,
      name: 'Instant Win Scratch',
      type: 'Instant',
      prize: '$10,000',
      ticketPrice: 3,
      drawTime: 'Instant',
      participants: 8900,
      odds: '1 in 100',
      description: 'Know instantly if you won',
      color: 'from-green-500 to-emerald-500',
    },
    {
      id: 5,
      name: 'Premium Gold Lottery',
      type: 'Weekly',
      prize: '$100,000',
      ticketPrice: 50,
      drawTime: '5d 2h',
      participants: 2100,
      odds: '1 in 2,100',
      description: 'Exclusive premium lottery with better odds',
      color: 'from-yellow-500 to-orange-500',
    },
    {
      id: 6,
      name: 'Charity Raffle',
      type: 'Monthly',
      prize: '$25,000',
      ticketPrice: 10,
      drawTime: '18d 12h',
      participants: 4500,
      odds: '1 in 4,500',
      description: '50% of proceeds go to charity',
      color: 'from-pink-500 to-rose-500',
    },
  ];

  const myTickets = [
    { id: 1, lottery: 'Daily Lucky Draw', ticketNumber: 'DL-2024-12345', drawDate: 'Today, 6:00 PM' },
    { id: 2, lottery: 'Weekly Mega Jackpot', ticketNumber: 'WM-2024-67890', drawDate: 'Dec 30, 2024' },
    { id: 3, lottery: 'Monthly Grand Prize', ticketNumber: 'MG-2024-11223', drawDate: 'Jan 8, 2025' },
  ];

  const handleBuyTickets = (lotteryId: number) => {
    const lottery = lotteries.find(l => l.id === lotteryId);
    const total = lottery ? lottery.ticketPrice * ticketCount : 0;
    console.log(`Buying ${ticketCount} ticket(s) for ${lottery?.name} - Total: $${total}`);
    // In production, this would call your Java backend API
    setSelectedLottery(null);
    setTicketCount(1);
  };

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl mb-2">Lottery Tickets</h1>
          <p className="text-gray-600">Choose your lucky numbers and win big prizes</p>
        </div>
        <div className="text-right">
          <div className="text-sm text-gray-600">Your Balance</div>
          <div className="text-2xl text-indigo-600">${user?.balance.toFixed(2)}</div>
        </div>
      </div>

      {/* My Active Tickets */}
      <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <h2 className="text-xl mb-4">My Active Tickets</h2>
        <div className="space-y-3">
          {myTickets.map((ticket) => (
            <div
              key={ticket.id}
              className="flex items-center justify-between p-4 bg-gradient-to-r from-indigo-50 to-purple-50 rounded-xl"
            >
              <div className="flex items-center space-x-4">
                <div className="p-2 bg-white rounded-lg">
                  <Ticket className="w-5 h-5 text-indigo-600" />
                </div>
                <div>
                  <div className="mb-1">{ticket.lottery}</div>
                  <div className="text-sm text-gray-600">{ticket.ticketNumber}</div>
                </div>
              </div>
              <div className="text-right">
                <div className="text-sm text-gray-600">Draw Date</div>
                <div className="text-sm">{ticket.drawDate}</div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Available Lotteries */}
      <div>
        <h2 className="text-xl mb-4">Available Lotteries</h2>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {lotteries.map((lottery) => (
            <div
              key={lottery.id}
              className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden hover:shadow-md transition-shadow"
            >
              <div className={`h-32 bg-gradient-to-r ${lottery.color} p-6 text-white`}>
                <div className="flex items-start justify-between mb-2">
                  <div className="text-xs px-2 py-1 bg-white/20 rounded-full">
                    {lottery.type}
                  </div>
                  <Ticket className="w-6 h-6" />
                </div>
                <div className="text-2xl">{lottery.name}</div>
              </div>
              <div className="p-6">
                <div className="mb-4">
                  <div className="text-3xl text-center mb-2">{lottery.prize}</div>
                  <div className="text-sm text-center text-gray-600">
                    {lottery.description}
                  </div>
                </div>

                <div className="space-y-2 mb-4 text-sm">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center text-gray-600">
                      <Clock className="w-4 h-4 mr-2" />
                      Draw in
                    </div>
                    <div>{lottery.drawTime}</div>
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center text-gray-600">
                      <Users className="w-4 h-4 mr-2" />
                      Participants
                    </div>
                    <div>{lottery.participants.toLocaleString()}</div>
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center text-gray-600">
                      <TrendingUp className="w-4 h-4 mr-2" />
                      Your odds
                    </div>
                    <div>{lottery.odds}</div>
                  </div>
                </div>

                <div className="pt-4 border-t border-gray-100">
                  <div className="flex items-center justify-between mb-3">
                    <div className="text-gray-600">Ticket Price</div>
                    <div className="text-xl">${lottery.ticketPrice}</div>
                  </div>
                  <button
                    onClick={() => setSelectedLottery(lottery.id)}
                    className={`w-full py-3 bg-gradient-to-r ${lottery.color} text-white rounded-xl hover:shadow-lg transition-shadow`}
                  >
                    Buy Tickets
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Buy Tickets Modal */}
      {selectedLottery && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl max-w-md w-full p-6">
            {(() => {
              const lottery = lotteries.find(l => l.id === selectedLottery);
              if (!lottery) return null;
              
              return (
                <>
                  <h3 className="text-xl mb-4">{lottery.name}</h3>
                  <div className="mb-6">
                    <label className="block text-sm text-gray-700 mb-2">
                      Number of Tickets
                    </label>
                    <input
                      type="number"
                      min="1"
                      max="100"
                      value={ticketCount}
                      onChange={(e) => setTicketCount(parseInt(e.target.value) || 1)}
                      className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-600"
                    />
                  </div>
                  <div className="mb-6 p-4 bg-gray-50 rounded-xl">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-gray-600">Price per ticket</span>
                      <span>${lottery.ticketPrice}</span>
                    </div>
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-gray-600">Quantity</span>
                      <span>{ticketCount}</span>
                    </div>
                    <div className="pt-2 border-t border-gray-200">
                      <div className="flex items-center justify-between">
                        <span>Total</span>
                        <span className="text-xl">${lottery.ticketPrice * ticketCount}</span>
                      </div>
                    </div>
                  </div>
                  <div className="flex space-x-3">
                    <button
                      onClick={() => setSelectedLottery(null)}
                      className="flex-1 py-3 border border-gray-200 rounded-xl hover:bg-gray-50"
                    >
                      Cancel
                    </button>
                    <button
                      onClick={() => handleBuyTickets(lottery.id)}
                      className={`flex-1 py-3 bg-gradient-to-r ${lottery.color} text-white rounded-xl hover:shadow-lg transition-shadow flex items-center justify-center space-x-2`}
                    >
                      <ShoppingCart className="w-5 h-5" />
                      <span>Purchase</span>
                    </button>
                  </div>
                </>
              );
            })()}
          </div>
        </div>
      )}
    </div>
  );
}
