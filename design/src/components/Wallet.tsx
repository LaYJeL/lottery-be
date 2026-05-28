import { useState } from 'react';
import { ArrowUpCircle, ArrowDownCircle, CreditCard, DollarSign, TrendingUp, Calendar } from 'lucide-react';

interface WalletProps {
  user: any;
}

export function Wallet({ user }: WalletProps) {
  const [showDepositModal, setShowDepositModal] = useState(false);
  const [showWithdrawModal, setShowWithdrawModal] = useState(false);
  const [amount, setAmount] = useState('');

  const transactions = [
    {
      id: 1,
      type: 'deposit',
      amount: 100,
      method: 'Credit Card',
      status: 'Completed',
      date: '2024-12-26',
      time: '14:30',
    },
    {
      id: 2,
      type: 'withdrawal',
      amount: 50,
      method: 'Bank Transfer',
      status: 'Processing',
      date: '2024-12-25',
      time: '09:15',
    },
    {
      id: 3,
      type: 'ticket_purchase',
      amount: -25,
      description: 'Weekly Mega Jackpot (3 tickets)',
      status: 'Completed',
      date: '2024-12-24',
      time: '18:45',
    },
    {
      id: 4,
      type: 'competition_entry',
      amount: -10,
      description: 'Photo Challenge 2024',
      status: 'Completed',
      date: '2024-12-23',
      time: '11:20',
    },
    {
      id: 5,
      type: 'reward',
      amount: 50,
      description: 'Weekly tasks completed',
      status: 'Completed',
      date: '2024-12-22',
      time: '00:00',
    },
    {
      id: 6,
      type: 'deposit',
      amount: 200,
      method: 'PayPal',
      status: 'Completed',
      date: '2024-12-20',
      time: '16:30',
    },
  ];

  const paymentMethods = [
    { id: 1, type: 'Credit Card', last4: '4242', icon: CreditCard, primary: true },
    { id: 2, type: 'Bank Account', last4: '1234', icon: DollarSign, primary: false },
  ];

  const handleDeposit = () => {
    console.log('Deposit:', amount);
    // In production, this would call your Java backend API
    setShowDepositModal(false);
    setAmount('');
  };

  const handleWithdraw = () => {
    console.log('Withdraw:', amount);
    // In production, this would call your Java backend API
    setShowWithdrawModal(false);
    setAmount('');
  };

  const getTransactionIcon = (type: string) => {
    switch (type) {
      case 'deposit':
        return <ArrowDownCircle className="w-5 h-5 text-green-600" />;
      case 'withdrawal':
        return <ArrowUpCircle className="w-5 h-5 text-blue-600" />;
      case 'reward':
        return <ArrowDownCircle className="w-5 h-5 text-purple-600" />;
      default:
        return <DollarSign className="w-5 h-5 text-gray-600" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Completed':
        return 'bg-green-100 text-green-700';
      case 'Processing':
        return 'bg-yellow-100 text-yellow-700';
      case 'Failed':
        return 'bg-red-100 text-red-700';
      default:
        return 'bg-gray-100 text-gray-700';
    }
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl mb-2">Wallet</h1>
        <p className="text-gray-600">Manage your balance and transactions</p>
      </div>

      {/* Balance Card */}
      <div className="bg-gradient-to-r from-indigo-600 to-purple-600 p-8 rounded-2xl text-white">
        <div className="flex items-center justify-between mb-8">
          <div>
            <div className="text-sm text-indigo-100 mb-2">Available Balance</div>
            <div className="text-4xl">${user?.balance.toFixed(2)}</div>
          </div>
          <div className="p-3 bg-white/20 rounded-xl">
            <DollarSign className="w-8 h-8" />
          </div>
        </div>
        <div className="flex space-x-3">
          <button
            onClick={() => setShowDepositModal(true)}
            className="flex-1 py-3 bg-white text-indigo-600 rounded-xl hover:shadow-lg transition-shadow flex items-center justify-center space-x-2"
          >
            <ArrowDownCircle className="w-5 h-5" />
            <span>Deposit</span>
          </button>
          <button
            onClick={() => setShowWithdrawModal(true)}
            className="flex-1 py-3 bg-indigo-500 text-white rounded-xl hover:bg-indigo-400 transition-colors flex items-center justify-center space-x-2"
          >
            <ArrowUpCircle className="w-5 h-5" />
            <span>Withdraw</span>
          </button>
        </div>
      </div>

      {/* Quick Stats */}
      <div className="grid md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
          <div className="flex items-center space-x-3 mb-2">
            <div className="p-2 bg-green-50 rounded-lg">
              <ArrowDownCircle className="w-5 h-5 text-green-600" />
            </div>
            <div className="text-sm text-gray-600">Total Deposited</div>
          </div>
          <div className="text-2xl">$1,500.00</div>
          <div className="text-sm text-green-600 flex items-center mt-1">
            <TrendingUp className="w-4 h-4 mr-1" />
            <span>This month</span>
          </div>
        </div>
        <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
          <div className="flex items-center space-x-3 mb-2">
            <div className="p-2 bg-blue-50 rounded-lg">
              <ArrowUpCircle className="w-5 h-5 text-blue-600" />
            </div>
            <div className="text-sm text-gray-600">Total Withdrawn</div>
          </div>
          <div className="text-2xl">$250.00</div>
          <div className="text-sm text-blue-600 flex items-center mt-1">
            <TrendingUp className="w-4 h-4 mr-1" />
            <span>This month</span>
          </div>
        </div>
        <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
          <div className="flex items-center space-x-3 mb-2">
            <div className="p-2 bg-purple-50 rounded-lg">
              <DollarSign className="w-5 h-5 text-purple-600" />
            </div>
            <div className="text-sm text-gray-600">Total Spent</div>
          </div>
          <div className="text-2xl">$450.00</div>
          <div className="text-sm text-purple-600 flex items-center mt-1">
            <TrendingUp className="w-4 h-4 mr-1" />
            <span>This month</span>
          </div>
        </div>
      </div>

      {/* Payment Methods */}
      <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <h2 className="text-xl mb-4">Payment Methods</h2>
        <div className="space-y-3">
          {paymentMethods.map((method) => {
            const Icon = method.icon;
            return (
              <div
                key={method.id}
                className="flex items-center justify-between p-4 bg-gray-50 rounded-xl"
              >
                <div className="flex items-center space-x-4">
                  <div className="p-2 bg-white rounded-lg">
                    <Icon className="w-5 h-5 text-gray-600" />
                  </div>
                  <div>
                    <div className="mb-1">{method.type}</div>
                    <div className="text-sm text-gray-600">•••• {method.last4}</div>
                  </div>
                </div>
                {method.primary && (
                  <div className="px-3 py-1 bg-indigo-100 text-indigo-700 rounded-full text-sm">
                    Primary
                  </div>
                )}
              </div>
            );
          })}
        </div>
        <button className="w-full mt-4 py-3 border border-gray-200 rounded-xl hover:bg-gray-50 text-gray-700">
          Add Payment Method
        </button>
      </div>

      {/* Transaction History */}
      <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl">Transaction History</h2>
          <Calendar className="w-5 h-5 text-gray-400" />
        </div>
        <div className="space-y-3">
          {transactions.map((transaction) => (
            <div
              key={transaction.id}
              className="flex items-center justify-between p-4 bg-gray-50 rounded-xl"
            >
              <div className="flex items-center space-x-4">
                <div className="p-2 bg-white rounded-lg">
                  {getTransactionIcon(transaction.type)}
                </div>
                <div>
                  <div className="mb-1">
                    {transaction.type === 'deposit' && `Deposit via ${transaction.method}`}
                    {transaction.type === 'withdrawal' && `Withdrawal to ${transaction.method}`}
                    {transaction.description}
                  </div>
                  <div className="text-sm text-gray-600">
                    {transaction.date} at {transaction.time}
                  </div>
                </div>
              </div>
              <div className="text-right">
                <div
                  className={`text-lg mb-1 ${
                    transaction.amount > 0 ? 'text-green-600' : 'text-gray-900'
                  }`}
                >
                  {transaction.amount > 0 ? '+' : ''}${Math.abs(transaction.amount).toFixed(2)}
                </div>
                <div
                  className={`inline-flex px-2 py-1 rounded-full text-xs ${getStatusColor(
                    transaction.status
                  )}`}
                >
                  {transaction.status}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Deposit Modal */}
      {showDepositModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl max-w-md w-full p-6">
            <h3 className="text-xl mb-4">Deposit Funds</h3>
            <div className="mb-6">
              <label className="block text-sm text-gray-700 mb-2">Amount</label>
              <div className="relative">
                <DollarSign className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="number"
                  min="10"
                  step="10"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-600"
                  placeholder="Enter amount"
                />
              </div>
              <div className="mt-3 flex flex-wrap gap-2">
                {[25, 50, 100, 200].map((value) => (
                  <button
                    key={value}
                    onClick={() => setAmount(value.toString())}
                    className="px-4 py-2 border border-gray-200 rounded-lg hover:bg-gray-50 text-sm"
                  >
                    ${value}
                  </button>
                ))}
              </div>
            </div>
            <div className="mb-6">
              <label className="block text-sm text-gray-700 mb-2">Payment Method</label>
              <select className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-600">
                <option>Credit Card ••••4242</option>
                <option>Bank Account ••••1234</option>
              </select>
            </div>
            <div className="flex space-x-3">
              <button
                onClick={() => setShowDepositModal(false)}
                className="flex-1 py-3 border border-gray-200 rounded-xl hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                onClick={handleDeposit}
                className="flex-1 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl hover:shadow-lg transition-shadow"
              >
                Deposit
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Withdraw Modal */}
      {showWithdrawModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl max-w-md w-full p-6">
            <h3 className="text-xl mb-4">Withdraw Funds</h3>
            <div className="mb-4 p-4 bg-yellow-50 rounded-xl">
              <div className="text-sm text-yellow-800">
                Withdrawals may take 3-5 business days to process
              </div>
            </div>
            <div className="mb-6">
              <label className="block text-sm text-gray-700 mb-2">Amount</label>
              <div className="relative">
                <DollarSign className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="number"
                  min="10"
                  max={user?.balance}
                  step="10"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-600"
                  placeholder="Enter amount"
                />
              </div>
              <div className="mt-2 text-sm text-gray-600">
                Available: ${user?.balance.toFixed(2)}
              </div>
            </div>
            <div className="mb-6">
              <label className="block text-sm text-gray-700 mb-2">Withdrawal Method</label>
              <select className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-600">
                <option>Bank Account ••••1234</option>
                <option>PayPal</option>
              </select>
            </div>
            <div className="flex space-x-3">
              <button
                onClick={() => setShowWithdrawModal(false)}
                className="flex-1 py-3 border border-gray-200 rounded-xl hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                onClick={handleWithdraw}
                className="flex-1 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl hover:shadow-lg transition-shadow"
              >
                Withdraw
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
